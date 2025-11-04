SELECT 
    Delivery_person_ID,
    AVG(Velocidade_KM_por_Minuto) AS velocidade_media
FROM velocidade_entregadores
GROUP BY Delivery_person_ID
ORDER BY velocidade_media DESC;