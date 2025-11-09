CREATE VIEW delivery_metrics_master AS
SELECT
  id,
  delivery_person_id,
  time_taken,
  
  -- Calculated Metric: Proxy Speed (Distance Squared / Time Taken)
  (
      ( (restaurant_latitude - delivery_location_latitude) * (restaurant_latitude - delivery_location_latitude) +
        (restaurant_longitude - delivery_location_longitude) * (restaurant_longitude - delivery_location_longitude) )
      / CAST(time_taken AS REAL)
  ) AS proxy_speed,
  
  -- Grouping Feature: Rating Range
  CASE
      WHEN delivery_person_ratings > 4.5 THEN 'high rating'
      WHEN delivery_person_ratings > 4.0 THEN 'medium rating'
      WHEN delivery_person_ratings > 3.5 THEN 'low rating'
      ELSE 'poor rating'
  END AS rating_range,
  
  -- Grouping Feature: Age Group
  CASE
      WHEN delivery_person_age < 25 THEN 'Ages 18-24'
      WHEN delivery_person_age < 35 THEN 'Ages 25-34'
      WHEN delivery_person_age >= 35 THEN 'Ages 35+'
      ELSE 'Unknown Age'
  END AS age_group,
  
  -- Grouping Feature: Adverse Condition Status
  CASE
      WHEN (weather_conditions IN ('conditions Stormy', 'conditions Sandstorms', 'conditions Fog', 'conditions Windy')
          OR road_traffic_density IN ('High', 'Jam'))
          THEN 'adverse_condition'
      ELSE 'normal_condition'
  END AS condition_status

FROM
  deliveries
WHERE
  -- Time Metrics
  time_taken IS NOT NULL
  AND time_taken > 0
  
  -- Delivery Person Metrics (Excluding NULL and 'NaN' string if it somehow appeared here)
  AND delivery_person_ratings IS NOT NULL
  AND delivery_person_ratings <> 'NaN'
  AND delivery_person_age IS NOT NULL
  AND delivery_person_age <> 'NaN'

  -- Location Coordinates (NULL check is sufficient, as coordinates are typically FLOAT/REAL)
  AND restaurant_latitude IS NOT NULL
  AND delivery_location_latitude IS NOT NULL
  
  -- Condition Metrics (Excluding NULL and the specific 'NaN' strings)
  AND weather_conditions IS NOT NULL
  AND weather_conditions <> 'conditions NaN'
  AND road_traffic_density IS NOT NULL
  AND road_traffic_density <> 'NaN';
