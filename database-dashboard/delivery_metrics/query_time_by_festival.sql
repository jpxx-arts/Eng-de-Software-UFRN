-- Query: Average delivery time and metrics by festival status
-- Shows impact of festivals on delivery performance
SELECT
    is_festival,
    CASE
        WHEN is_festival = 1 THEN 'Festival'
        ELSE 'Sem Festival'
    END AS festival_status,
    AVG(delivery_time) AS avg_delivery_time,
    AVG(time_taken) AS avg_time_taken,
    AVG(speed_kmh) AS avg_speed_kmh,
    COUNT(id) AS total_deliveries
FROM
    delivery_metrics_master
GROUP BY
    is_festival
ORDER BY
    is_festival;