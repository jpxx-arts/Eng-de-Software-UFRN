package com.umbrella.tomaladaka.service;

import com.umbrella.tomaladaka.model.Menu;
import com.umbrella.tomaladaka.model.Restaurant;
import com.umbrella.tomaladaka.repository.RestaurantRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepo;

    public RestaurantService(RestaurantRepository restaurantRepo) {
        this.restaurantRepo = restaurantRepo;
    }

    public Restaurant createRestaurant(Restaurant restaurant) {
        Menu newMenu = new Menu();
        restaurant.setMenu(newMenu);
        newMenu.setRestaurant(restaurant);
        return restaurantRepo.save(restaurant);
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