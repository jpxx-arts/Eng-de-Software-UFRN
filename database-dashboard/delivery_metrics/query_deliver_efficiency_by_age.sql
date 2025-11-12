SELECT
  age_group,
  AVG(proxy_speed) AS average_speed, -- degree²/min 
  COUNT(id) AS total_deliveries
FROM
  delivery_metrics_master
GROUP BY
  age_group
ORDER BY
  average_speed DESC;