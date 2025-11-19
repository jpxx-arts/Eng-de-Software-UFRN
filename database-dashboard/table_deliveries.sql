CREATE TABLE
    deliveries (
        id PRIMARY KEY,
        delivery_person_id TEXT,
        delivery_person_age INT,
        delivery_person_ratings FLOAT,
        restaurant_latitude FLOAT,
        restaurant_longitude FLOAT,
        delivery_location_latitude FLOAT,
        delivery_location_longitude FLOAT,
        order_date DATE, -- Stored internally as TEXT in our case (ISO86O1)
        time_orderd TEXT,
        time_order_picked TEXT,
        weather_conditions TEXT,
        road_traffic_density TEXT,
        vehicle_condition INT,
        type_of_order TEXT,
        type_of_vehicle TEXT,
        multiple_deliveries INT,
        festival TEXT,
        city TEXT,
        time_taken INT,
        preparing_time INT,
        delta_time INT
    );