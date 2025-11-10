SELECT
  speed_kmh
FROM
  delivery_metrics_master
WHERE
  speed_kmh IS NOT NULL
  AND speed_kmh >= 0
ORDER BY
  speed_kmh;