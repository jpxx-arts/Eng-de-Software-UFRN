package com.umbrella.tomaladaka.controller;

import com.umbrella.tomaladaka.model.Order;
import com.umbrella.tomaladaka.model.Menu;
import com.umbrella.tomaladaka.model.Restaurant;
import com.umbrella.tomaladaka.service.OrderService;
import com.umbrella.tomaladaka.service.MenuService;
import com.umbrella.tomaladaka.service.RestaurantService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final OrderService orderService;
    private final MenuService menuService;

    public RestaurantController(RestaurantService restaurantService, OrderService orderService,
            MenuService menuService) {
        this.restaurantService = restaurantService;
        this.orderService = orderService;
        this.menuService = menuService;

    }

    @GetMapping("/{id}")
    public ResponseEntity<Restaurant> getRestaurantById(@PathVariable Long id) {
        Restaurant restaurant = restaurantService.getRestaurantById(id);
        return ResponseEntity.ok(restaurant);
    }

    @GetMapping
    public ResponseEntity<List<Restaurant>> getAllRestaurants() {
        List<Restaurant> restaurants = restaurantService.getAllRestaurants();
        return ResponseEntity.ok(restaurants);
    }

    @PostMapping
    public ResponseEntity<Restaurant> createRestaurant(@RequestBody Restaurant restaurant) {
        // Agora chama o serviço
        Restaurant savedRestaurant = restaurantService.createRestaurant(restaurant);
        URI location = URI.create(String.format("/restaurants/%d", savedRestaurant.getId()));
        return ResponseEntity.created(location).body(savedRestaurant);
    }

    @PostMapping("/{restaurantId}/orders/{orderId}/accept")
    public ResponseEntity<Order> acceptOrder(
            @PathVariable Long restaurantId,
            @PathVariable Long orderId) {

        Order updatedOrder = orderService.acceptOrder(orderId, restaurantId);
        return ResponseEntity.ok(updatedOrder);
    }

    @PostMapping("/{restaurantId}/orders/{orderId}/finish-preparation")
    public ResponseEntity<Order> finishOrderPreparation(
            @PathVariable Long restaurantId,
            @PathVariable Long orderId) {

        Order updatedOrder = orderService.finishOrderPreparation(orderId, restaurantId);
        return ResponseEntity.ok(updatedOrder);
    }

    @PostMapping("/{restaurantId}/menu/items")
    public ResponseEntity<Menu> addItemToMenu(
            @PathVariable Long restaurantId,
            @RequestParam Long itemId) {

        Menu updatedMenu = menuService.addItemToRestaurantMenu(restaurantId, itemId);
        return ResponseEntity.ok(updatedMenu);
    }

}