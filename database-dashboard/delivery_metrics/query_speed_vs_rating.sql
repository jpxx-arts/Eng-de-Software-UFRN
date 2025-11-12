SELECT
  delivery_person_id,
  AVG(speed_kmh) AS avg_speed_kmh,
  AVG(delivery_person_ratings) AS avg_rating,
  COUNT(*) AS deliveries
FROM
  delivery_metrics_master
WHERE
  speed_kmh IS NOT NULL
  AND delivery_person_ratings IS NOT NULL
  AND speed_kmh < 200
GROUP BY
  delivery_person_id
HAVING
  COUNT(*) >= 5
ORDER BY
  avg_rating DESC;