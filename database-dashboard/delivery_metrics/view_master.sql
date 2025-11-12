CREATE VIEW
  delivery_metrics_master AS
SELECT
  id,
  delivery_person_id,
  time_taken,
  CASE
    WHEN (festival = "Yes") THEN 1
    ELSE 0
  END AS is_festival,
  delta_time AS delivery_time,
  (
    (
      (restaurant_latitude - delivery_location_latitude) * (restaurant_latitude - delivery_location_latitude) + (
        restaurant_longitude - delivery_location_longitude
      ) * (
        restaurant_longitude - delivery_location_longitude
      )
    ) / CAST(delta_time AS REAL)
  ) AS proxy_speed,
  (
    6371.0 * ACOS(
      COS(RADIANS (delivery_location_latitude)) * COS(RADIANS (restaurant_latitude)) * COS(
        RADIANS (restaurant_longitude) - RADIANS (delivery_location_longitude)
      ) + SIN(RADIANS (delivery_location_latitude)) * SIN(RADIANS (restaurant_latitude))
    )
  ) AS distance_km,
  (
    (
      6371.0 * ACOS(
        COS(RADIANS (delivery_location_latitude)) * COS(RADIANS (restaurant_latitude)) * COS(
          RADIANS (restaurant_longitude) - RADIANS (delivery_location_longitude)
        ) + SIN(RADIANS (delivery_location_latitude)) * SIN(RADIANS (restaurant_latitude))
      )
    ) / (CAST(delta_time AS REAL) / 60.0)
  ) AS speed_kmh,
  CASE
    WHEN delivery_person_ratings > 4.5 THEN 'high rating'
    WHEN delivery_person_ratings > 4.0 THEN 'medium rating'
    WHEN delivery_person_ratings > 3.5 THEN 'low rating'
    ELSE 'poor rating'
  END AS rating_range,
  CASE
    WHEN delivery_person_age < 25 THEN 'Ages 18-24'
    WHEN delivery_person_age < 35 THEN 'Ages 25-34'
    WHEN delivery_person_age >= 35 THEN 'Ages 35+'
    ELSE 'Unknown Age'
  END AS age_group,
  CASE
    WHEN (
      weather_conditions IN (
        'conditions Stormy',
        'conditions Sandstorms',
        'conditions Fog',
        'conditions Windy'
      )
      OR road_traffic_density IN ('High', 'Jam')
    ) THEN 'adverse_condition'
    ELSE 'normal_condition'
  END AS condition_status,
  delivery_person_ratings
FROM
  deliveries
WHERE
  time_taken IS NOT NULL
  AND time_taken > 0
  AND delivery_person_ratings IS NOT NULL
  AND delivery_person_ratings <> 'NaN'
  AND delivery_person_age IS NOT NULL
  AND delivery_person_age <> 'NaN'
  AND restaurant_latitude IS NOT NULL
  AND delivery_location_latitude IS NOT NULL
  AND weather_conditions IS NOT NULL
  AND weather_conditions <> 'conditions NaN'
  AND road_traffic_density IS NOT NULL
  AND road_traffic_density <> 'NaN'
  AND delta_time > 0;