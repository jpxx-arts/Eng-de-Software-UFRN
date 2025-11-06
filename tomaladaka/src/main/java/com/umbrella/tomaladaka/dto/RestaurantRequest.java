package com.umbrella.tomaladaka.dto;

import com.umbrella.tomaladaka.model.Address;

import lombok.Data;

@Data
public class RestaurantRequest {
    String name;
    String phone;
    Address address;
}

