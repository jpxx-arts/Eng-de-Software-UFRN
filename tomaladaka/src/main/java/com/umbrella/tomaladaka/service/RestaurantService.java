package com.umbrella.tomaladaka.service;

import com.umbrella.tomaladaka.dto.RestaurantRequest;
import com.umbrella.tomaladaka.model.Menu;
import com.umbrella.tomaladaka.model.Restaurant;
import com.umbrella.tomaladaka.repository.MenuRepository;
import com.umbrella.tomaladaka.repository.RestaurantRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepo;
    private final MenuRepository menuRepository;

    public Restaurant createRestaurant(RestaurantRequest restaurantDTO) {
        Menu menu = new Menu();

        Restaurant restaurant = Restaurant.builder()
            .name(restaurantDTO.getName())
            .phone(restaurantDTO.getPhone())
            .menu(menu)
            .build();

        Restaurant createdRestaurant = restaurantRepo.save(restaurant);

        menu.setRestaurant(createdRestaurant);
        menuRepository.save(menu);
        return createdRestaurant;
    }

    public Restaurant getRestaurantById(Long id) {
        return restaurantRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Restaurant not found: " + id));
    }

    public List<Restaurant> getAllRestaurants() {
        return restaurantRepo.findAll();
    }

    public Restaurant updateRestaurantDetails(Long id, Restaurant restaurantDetails) {
        Restaurant existingRestaurant = getRestaurantById(id);

        if (restaurantDetails.getName() != null) {
            existingRestaurant.setName(restaurantDetails.getName());
        }
        if (restaurantDetails.getAddress() != null) {
            existingRestaurant.setAddress(restaurantDetails.getAddress());
        }
        if (restaurantDetails.getPhone() != null) {
            existingRestaurant.setPhone(restaurantDetails.getPhone());
        }

        return restaurantRepo.save(existingRestaurant);
    }

    public void deleteRestaurant(Long id) {
        Restaurant restaurant = getRestaurantById(id);
        restaurantRepo.delete(restaurant);
    }
}