SELECT
  delivery_person_id,
  AVG(speed_kmh) AS avg_speed_kmh,
  COUNT(*) AS deliveries,
  AVG(time_taken) AS avg_time_min,
  SUM(distance_km) AS total_distance_km
FROM
  delivery_metrics_master
WHERE
  speed_kmh IS NOT NULL
  AND time_taken IS NOT NULL
  AND time_taken > 0
  AND speed_kmh < 200
GROUP BY
  delivery_person_id
HAVING
  COUNT(*) > 0
ORDER BY
  avg_speed_kmh DESC
LIMIT
  10;