SELECT
  rating_range,
  AVG(proxy_speed) AS average_speed, -- degree²/min
  COUNT(id) AS total_deliveries
FROM
  delivery_metrics_master
GROUP BY
  rating_range
ORDER BY
  average_speed DESC;
