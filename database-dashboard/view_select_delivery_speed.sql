CREATE VIEW velocidade_entregadores AS
SELECT
    Delivery_person_ID,
    Delivery_person_Age,
    -- 1. Cálculo da Distância (usando a fórmula de Haversine em km)
    -- Multiplicamos por 6371 (raio da Terra em km)
    (
        6371 * ACOS(
            COS(RADIANS(Delivery_location_latitude)) * COS(RADIANS(Restaurant_latitude)) *
            COS(RADIANS(Restaurant_longitude) - RADIANS(Delivery_location_longitude)) +
            SIN(RADIANS(Delivery_location_latitude)) * SIN(RADIANS(Restaurant_latitude))
        )
    ) AS Distancia_KM,
    
    -- 2. Cálculo da Velocidade (Distancia_KM / Tempo_gasto)
    Time_taken,
    (
        (
            6371 * ACOS(
                COS(RADIANS(Delivery_location_latitude)) * COS(RADIANS(Restaurant_latitude)) *
                COS(RADIANS(Restaurant_longitude) - RADIANS(Delivery_location_longitude)) +
                SIN(RADIANS(Delivery_location_latitude)) * SIN(RADIANS(Restaurant_latitude))
            )
        ) / Time_taken
    ) AS Velocidade_KM_por_Minuto
FROM delivery;
-- WHERE Time_taken > 40;