package com.umbrella.tomaladaka.controller;

import com.umbrella.tomaladaka.model.Restaurant;
import com.umbrella.tomaladaka.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/restaurants")
public class RestaurantController {

  @Autowired
  private RestaurantRepository restaurantRepository;

  @PostMapping
  public ResponseEntity<Restaurant> createRestaurant(@RequestBody Restaurant restaurant) {
    Restaurant savedRestaurant = restaurantRepository.save(restaurant);
    URI location = URI.create(String.format("/restaurants/%d", savedRestaurant.getId()));
    return ResponseEntity.created(location).body(savedRestaurant); // it returns 201 CREATED
  }
}
