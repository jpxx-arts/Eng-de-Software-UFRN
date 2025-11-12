import streamlit as st
import pandas as pd
import plotly.express as px
import plotly.graph_objects as go
import numpy as np
import sqlite3
import os

st.set_page_config(page_title="Dashboard de Entregas", layout="wide")

DB_PATH = 'delivery.db'

TABLE_DELIVERIES_SQL = 'table_deliveries.sql'
DATA_CSV = 'food_delivery_dataset.csv'

VIEW_MASTER_SQL = os.path.join('delivery_metrics', 'view_master.sql')
QUERY_TIME_CONDITIONS_SQL = os.path.join('delivery_metrics', 'query_time_taken_by_weather_conditions.sql')
QUERY_EFF_RATING_SQL = os.path.join('delivery_metrics', 'query_deliver_efficiency_by_rating.sql')
QUERY_EFF_AGE_SQL = os.path.join('delivery_metrics', 'query_deliver_efficiency_by_age.sql')
QUERY_SPEED_HISTOGRAM_SQL = os.path.join('delivery_metrics', 'query_speed_histogram.sql')
QUERY_SPEED_VS_RATING_SQL = os.path.join('delivery_metrics', 'query_speed_vs_rating.sql')
QUERY_TOP_FASTEST_SQL = os.path.join('delivery_metrics', 'query_top_fastest_delivery_persons.sql')
QUERY_TOP_SLOWEST_SQL = os.path.join('delivery_metrics', 'query_top_slowest_delivery_persons.sql')
QUERY_TIME_BY_FESTIVAL_SQL = os.path.join('delivery_metrics', 'query_time_by_festival.sql')

@st.cache_resource
def get_connection(db_path):
    return sqlite3.connect(db_path, check_same_thread=False)

def read_sql_file(filepath):
    try:
        with open(filepath, 'r') as f:
            return f.read()
    except FileNotFoundError:
        st.error(f"Erro: Arquivo SQL não encontrado em '{filepath}'")
        st.stop()
    except Exception as e:
        st.error(f"Erro ao ler o arquivo {filepath}: {e}")
        st.stop()

@st.cache_resource(show_spinner="Verificando banco de dados...")
def initialize_database(_conn):
    cursor = _conn.cursor()
    
    cursor.execute("SELECT name FROM sqlite_master WHERE type='table' AND name='deliveries'")
    if cursor.fetchone() is not None:
        return
        
    try:
        st.warning(f"Tabela 'deliveries' não encontrada. Criando e importando dados de '{DATA_CSV}'...")
        create_table_sql = read_sql_file(TABLE_DELIVERIES_SQL)
        cursor.executescript(create_table_sql) 
        
        df = pd.read_csv(DATA_CSV)
        
        df.columns = df.columns.str.strip()
        
        df = df.replace('NaN', pd.NA) 
        
        df['delivery_person_age'] = pd.to_numeric(df['delivery_person_age'], errors='coerce')
        df['delivery_person_ratings'] = pd.to_numeric(df['delivery_person_ratings'], errors='coerce')
        df['multiple_deliveries'] = pd.to_numeric(df['multiple_deliveries'], errors='coerce')
        df['time_taken'] = pd.to_numeric(df['time_taken'], errors='coerce')
        df['vehicle_condition'] = pd.to_numeric(df['vehicle_condition'], errors='coerce')
        
        df['weather_conditions'] = df['weather_conditions'].str.strip()
        df['road_traffic_density'] = df['road_traffic_density'].str.strip()
        
        df = df.rename(columns={
            'ID': 'id',
            'Delivery_person_ID': 'delivery_person_id',
            'Delivery_person_Age': 'delivery_person_age',
            'Delivery_person_Ratings': 'delivery_person_ratings',
            'Restaurant_latitude': 'restaurant_latitude',
            'Restaurant_longitude': 'restaurant_longitude',
            'Delivery_location_latitude': 'delivery_location_latitude',
            'Delivery_location_longitude': 'delivery_location_longitude',
            'Order_Date': 'order_date',
            'Time_Orderd': 'time_orderd',
            'Time_Order_picked': 'time_order_picked',
            'Weatherconditions': 'weather_conditions',
            'Weather_conditions': 'weather_conditions',
            'Road_traffic_density': 'road_traffic_density',
            'Vehicle_condition': 'vehicle_condition',
            'Type_of_order': 'type_of_order',
            'Type_of_vehicle': 'type_of_vehicle',
            'multiple_deliveries': 'multiple_deliveries',
            'Festival': 'festival',
            'City': 'city',
            'Time_taken(min)': 'time_taken'
        })

        cursor.execute("PRAGMA table_info(deliveries)")
        table_columns = [row[1] for row in cursor.fetchall()]
        
        df_final = df[df.columns.intersection(table_columns)]

        if 'id' not in df_final.columns or 'time_taken' not in df_final.columns:
            st.error("Erro na importação do CSV: Colunas essenciais (ex: 'id', 'time_taken') não foram encontradas no CSV ou não bateram com o schema da tabela.")
            st.info(f"Colunas do CSV detectadas: {list(df.columns)}")
            st.info(f"Colunas da tabela esperadas: {table_columns}")
            st.stop()

        df_final.to_sql('deliveries', _conn, if_exists='append', index=False)
        _conn.commit()
        st.success(f"Tabela 'deliveries' criada e {len(df_final)} registros importados com sucesso!")
        st.rerun()
        
    except FileNotFoundError as e:
        st.error(f"Erro na inicialização: Arquivo não encontrado: {e.filename}")
        st.info(f"Certifique-se que '{TABLE_DELIVERIES_SQL}' e '{DATA_CSV}' estão no mesmo diretório do 'app.py'.")
        st.stop()
    except pd.errors.EmptyDataError:
        st.error(f"Erro: O arquivo CSV '{DATA_CSV}' está vazio.")
        st.stop()
    except Exception as e:
        st.error(f"Erro inesperado durante a inicialização do banco de dados: {e}")
        st.stop()


def setup_database(conn):
    try:
        view_sql = read_sql_file(VIEW_MASTER_SQL)
        view_sql_if_not_exists = view_sql.replace("CREATE VIEW", "CREATE VIEW IF NOT EXISTS")
        cursor = conn.cursor()
        cursor.execute(view_sql_if_not_exists)
        conn.commit()
    except Exception as e:
        st.error(f"Erro ao criar a VIEW 'delivery_metrics_master': {e}")
        st.info("Verifique se a tabela 'deliveries' existe e se o .sql da VIEW está correto.")
        st.stop()

@st.cache_data(ttl=300)
def load_data_from_query(_conn, query_path):
    query = read_sql_file(query_path)
    try:
        df = pd.read_sql_query(query, _conn)
        return df
    except Exception as e:
        st.error(f"Erro ao executar a query de '{query_path}': {e}")
        st.info("Isso geralmente acontece se a VIEW 'delivery_metrics_master' falhou ao ser criada.")
        return pd.DataFrame()

@st.cache_data(ttl=300)
def load_raw_view_data(_conn):
    try:
        df = pd.read_sql_query("SELECT * FROM delivery_metrics_master LIMIT 1000", _conn)
        return df
    except Exception as e:
        st.warning(f"Não foi possível carregar os dados brutos da view: {e}")
        return pd.DataFrame()


st.title("🚛 Dashboard de Métricas de Entrega")

conn = get_connection(DB_PATH)

initialize_database(conn) 

setup_database(conn)

df_conditions = load_data_from_query(conn, QUERY_TIME_CONDITIONS_SQL)
df_rating = load_data_from_query(conn, QUERY_EFF_RATING_SQL)
df_age = load_data_from_query(conn, QUERY_EFF_AGE_SQL)

st.header("📈 KPIs Principais")

try:
    total_deliveries = df_conditions['total_deliveries'].sum()
    avg_time_normal = df_conditions[df_conditions['condition_status'] == 'normal_condition']['average_time_minutes'].values[0]
    avg_time_adverse = df_conditions[df_conditions['condition_status'] == 'adverse_condition']['average_time_minutes'].values[0]
    time_delta = avg_time_adverse - avg_time_normal
    
    col1, col2, col3 = st.columns(3)
    col1.metric("Total de Entregas Analisadas", f"{total_deliveries:,.0f}")
    col2.metric("Tempo Médio (Normal)", f"{avg_time_normal:.2f} min")
    col3.metric("Tempo Médio (Adverso)", f"{avg_time_adverse:.2f} min", 
                 delta=f"{time_delta:.2f} min (pior)", delta_color="inverse")
except (IndexError, KeyError, TypeError):
    st.warning("Não foi possível calcular os KPIs. Verifique se as queries retornaram dados.")

st.markdown("---")

col_left, col_right = st.columns(2)

with col_left:
    st.subheader("Impacto das Condições no Tempo de Entrega")
    if not df_conditions.empty:
        st.bar_chart(df_conditions.set_index('condition_status'), y='average_time_minutes')
        st.dataframe(df_conditions, use_container_width=True)
    else:
        st.warning("Não há dados para a análise de condições.")

    st.subheader("Eficiência por Faixa Etária")
    if not df_age.empty:
        st.bar_chart(df_age.set_index('age_group'), y='average_speed')
        st.dataframe(df_age, use_container_width=True)
    else:
        st.warning("Não há dados para a análise de eficiência por idade.")

with col_right:
    st.subheader("Eficiência por Classificação (Rating)")
    if not df_rating.empty:
        rating_order = ['poor rating', 'low rating', 'medium rating', 'high rating']
        try:
            df_rating_sorted = df_rating.set_index('rating_range').reindex(rating_order).dropna()
            st.bar_chart(df_rating_sorted, y='average_speed')
        except:
            st.bar_chart(df_rating.set_index('rating_range'), y='average_speed')
            
        st.dataframe(df_rating, use_container_width=True)
    else:
        st.warning("Não há dados para a análise de eficiência por rating.")

st.markdown("---")
st.markdown("### 🎉 Impacto de Festivais no Tempo de Entrega")
try:
    df_festival = load_data_from_query(conn, QUERY_TIME_BY_FESTIVAL_SQL)
    if not df_festival.empty:
        df_festival['avg_delivery_time'] = df_festival['avg_delivery_time'].round(2)
        df_festival['avg_time_taken'] = df_festival['avg_time_taken'].round(2)
        df_festival['avg_speed_kmh'] = df_festival['avg_speed_kmh'].round(2)
        
        festival_data = df_festival[df_festival['is_festival'] == 1]
        normal_data = df_festival[df_festival['is_festival'] == 0]
        
        avg_time_festival = festival_data['avg_time_taken'].values[0] if not festival_data.empty else 0
        avg_time_normal = normal_data['avg_time_taken'].values[0] if not normal_data.empty else 0
        time_delta_festival = avg_time_festival - avg_time_normal
        
        col_fest_metric1, col_fest_metric2 = st.columns(2)
        with col_fest_metric1:
            st.metric("Tempo Médio (Sem Festival)", f"{avg_time_normal:.2f} min")
        with col_fest_metric2:
            st.metric("Tempo Médio (Com Festival)", f"{avg_time_festival:.2f} min", 
                     delta=f"{time_delta_festival:.2f} min (impacto)", delta_color="inverse" if time_delta_festival > 0 else "off")
        
        col_fest_metrics, col_fest_chart = st.columns(2)
        
        with col_fest_metrics:
            st.subheader("Métricas por Status de Festival")
            st.dataframe(df_festival[['festival_status', 'avg_delivery_time', 'avg_speed_kmh', 'total_deliveries']], use_container_width=True)
        
        with col_fest_chart:
            st.subheader("Comparativo de Tempo Médio")
            fig_festival = px.bar(df_festival, x='festival_status', y='avg_time_taken', 
                                 title='Tempo Médio de Entrega: Festival vs Normal',
                                 labels={'festival_status': 'Status', 'avg_time_taken': 'Tempo (minutos)'})
            st.plotly_chart(fig_festival, use_container_width=True)
    else:
        st.warning("Sem dados para análise de impacto de festivais.")
except Exception as e:
    st.warning(f"Erro ao carregar dados de festivais: {e}")


st.markdown("---")

st.markdown("### 🏁 Top 10 Entregadores — Mais Rápidos e Mais Lentos")
try:
    df_fast = load_data_from_query(conn, QUERY_TOP_FASTEST_SQL)
    if not df_fast.empty:
        df_fast['avg_speed_kmh'] = df_fast['avg_speed_kmh'].round(2)
        df_fast['avg_time_min'] = df_fast['avg_time_min'].round(2)
        df_fast['total_distance_km'] = df_fast['total_distance_km'].round(2)

    df_slow = load_data_from_query(conn, QUERY_TOP_SLOWEST_SQL)
    if not df_slow.empty:
        df_slow['avg_speed_kmh'] = df_slow['avg_speed_kmh'].round(2)
        df_slow['avg_time_min'] = df_slow['avg_time_min'].round(2)
        df_slow['total_distance_km'] = df_slow['total_distance_km'].round(2)

    col_fast, col_slow = st.columns(2)
    with col_fast:
        st.subheader("Top 10 — Mais Rápidos")
        if not df_fast.empty:
            st.dataframe(df_fast, use_container_width=True)
        else:
            st.warning("Sem dados para entregadores mais rápidos.")

    with col_slow:
        st.subheader("Top 10 — Mais Lentos")
        if not df_slow.empty:
            st.dataframe(df_slow, use_container_width=True)
        else:
            st.warning("Sem dados para entregadores mais lentos.")
except Exception as e:
    st.warning(f"Não foi possível carregar os rankings dos entregadores: {e}")

st.markdown("---")
st.markdown("### 📊 Visualizações adicionais")
try:
    max_speed = st.slider("Filtro: velocidade máxima (km/h) para visualizações", min_value=50, max_value=300, value=150)

    df_hist = load_data_from_query(conn, QUERY_SPEED_HISTOGRAM_SQL)
    if not df_hist.empty:
        df_hist_filtered = df_hist[df_hist['speed_kmh'] <= max_speed]
        if not df_hist_filtered.empty:
            fig_hist = px.histogram(df_hist_filtered, x='speed_kmh', nbins=40, title=f'Distribuição de velocidade (<= {max_speed} km/h)')
            st.plotly_chart(fig_hist, use_container_width=True)
        else:
            st.info("Sem dados válidos para o histograma de velocidade no intervalo selecionado.")
    else:
        st.info("Sem dados para o histograma de velocidade.")

    df_scatter = load_data_from_query(conn, QUERY_SPEED_VS_RATING_SQL)
    if not df_scatter.empty:
        x = df_scatter['avg_rating'].astype(float).to_numpy()
        y = df_scatter['avg_speed_kmh'].astype(float).to_numpy()

        if len(x) >= 2:
            slope, intercept = np.polyfit(x, y, 1)
            xs = np.linspace(x.min(), x.max(), 100)
            ys = slope * xs + intercept
            try:
                r = np.corrcoef(x, y)[0, 1]
                r2 = r**2
            except Exception:
                r2 = None

            fig_scatter = px.scatter(df_scatter, x='avg_rating', y='avg_speed_kmh', size='deliveries', hover_data=['delivery_person_id', 'deliveries'],
                                     title='Velocidade média por entregador vs Rating (min 5 entregas)')
            fig_scatter.add_trace(go.Scatter(x=xs, y=ys, mode='lines', name='Regressão linear', line=dict(color='red')))
            if r2 is not None:
                fig_scatter.update_layout(title=f'Velocidade média por entregador vs Rating (min 5 entregas) — R²={r2:.2f}')
        else:
            fig_scatter = px.scatter(df_scatter, x='avg_rating', y='avg_speed_kmh', size='deliveries', hover_data=['delivery_person_id', 'deliveries'],
                                     title='Velocidade média por entregador vs Rating (insuficiente para regressão)')

        st.plotly_chart(fig_scatter, use_container_width=True)
    else:
        st.info("Sem entregadores com pelo menos 5 entregas ou dados insuficientes para o gráfico.")
except Exception as e:
    st.warning(f"Erro ao gerar visualizações adicionais: {e}")

st.markdown("---")
with st.expander("🕵️‍♂️ Explorar Dados Brutos (View `delivery_metrics_master`)"):
    st.write("""
    Abaixo estão os primeiros 1.000 registros da view principal que alimenta este dashboard. 
    Isso pode ajudar a verificar os dados limpos e as colunas calculadas.
    """)
    if st.checkbox("Carregar dados brutos da view"):
        df_raw = load_raw_view_data(conn)
        st.dataframe(df_raw, use_container_width=True)
