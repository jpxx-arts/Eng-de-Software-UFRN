import streamlit as st
import pandas as pd
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

@st.cache_resource
def get_connection(db_path):
    """Cria e armazena em cache a conexão com o banco de dados."""
    return sqlite3.connect(db_path, check_same_thread=False)

def read_sql_file(filepath):
    """Lê um arquivo SQL e retorna seu conteúdo como string."""
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
    """
    Verifica se a tabela 'deliveries' existe. Se não, cria a tabela
    e a popula a partir do 'food_delivery_dataset.csv'.
    """
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
    """
    Garante que a VIEW principal exista no banco de dados.
    Usa 'CREATE VIEW IF NOT EXISTS' para ser idempotente.
    """
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
    """
    Carrega dados do banco de dados executando um arquivo de query.
    O argumento _conn é ignorado pelo cache do Streamlit.
    """
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
    """Carrega uma amostra de dados brutos da VIEW para exploração."""
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
with st.expander("🕵️‍♂️ Explorar Dados Brutos (View `delivery_metrics_master`)"):
    st.write("""
    Abaixo estão os primeiros 1.000 registros da view principal que alimenta este dashboard. 
    Isso pode ajudar a verificar os dados limpos e as colunas calculadas.
    """)
    if st.checkbox("Carregar dados brutos da view"):
        df_raw = load_raw_view_data(conn)
        st.dataframe(df_raw, use_container_width=True)
