SELECT
  condition_status,
  AVG(time_taken) AS average_time_minutes, -- min
  COUNT(id) AS total_deliveries
FROM
  delivery_metrics_master
GROUP BY
  condition_status;
