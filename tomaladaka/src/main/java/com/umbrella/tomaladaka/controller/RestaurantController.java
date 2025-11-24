package com.umbrella.tomaladaka.controller;

import com.umbrella.tomaladaka.model.Order;
import com.umbrella.tomaladaka.dto.RestaurantRequest;
import com.umbrella.tomaladaka.model.Menu;
import com.umbrella.tomaladaka.model.Restaurant;
import com.umbrella.tomaladaka.service.OrderService;
import com.umbrella.tomaladaka.service.MenuService;
import com.umbrella.tomaladaka.service.RestaurantService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
@Tag(name = "Restaurant Controller", description = "Manage restaurants, menus, and orders")
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final OrderService orderService;
    private final MenuService menuService;

    @GetMapping("/{id}")
    @Operation(summary = "Get restaurant by ID", description = "Retrieve a specific restaurant by its ID")
    @ApiResponse(responseCode = "200", description = "Restaurant found")
    @ApiResponse(responseCode = "404", description = "Restaurant not found")
    public ResponseEntity<Restaurant> getRestaurantById(@PathVariable Long id) {
        Restaurant restaurant = restaurantService.getRestaurantById(id);
        return ResponseEntity.ok(restaurant);
    }

    @GetMapping
    @Operation(summary = "Get all restaurants", description = "Retrieve a list of all registered restaurants")
    @ApiResponse(responseCode = "200", description = "List of restaurants retrieved")
    public ResponseEntity<List<Restaurant>> getAllRestaurants() {
        List<Restaurant> restaurants = restaurantService.getAllRestaurants();
        return ResponseEntity.ok(restaurants);
    }

    @PostMapping
    @Operation(summary = "Create a new restaurant", description = "Register a new restaurant")
    @ApiResponse(responseCode = "201", description = "Restaurant created successfully")
    public ResponseEntity<Restaurant> createRestaurant(@RequestBody RestaurantRequest restaurantDTO) {
        // Agora chama o serviço
        Restaurant savedRestaurant = restaurantService.createRestaurant(restaurantDTO);
        URI location = URI.create(String.format("/restaurants/%d", savedRestaurant.getId()));
        return ResponseEntity.created(location).body(savedRestaurant);
    }

    @PostMapping("/{restaurantId}/orders/{orderId}/accept")
    @Operation(summary = "Accept an order", description = "Restaurant accepts an incoming order")
    @ApiResponse(responseCode = "200", description = "Order accepted")
    public ResponseEntity<Order> acceptOrder(
            @PathVariable Long restaurantId,
            @PathVariable Long orderId) {

        Order updatedOrder = orderService.acceptOrder(orderId, restaurantId);
        return ResponseEntity.ok(updatedOrder);
    }

    @PostMapping("/{restaurantId}/orders/{orderId}/finish-preparation")
    @Operation(summary = "Finish order preparation", description = "Mark an order as prepared by the restaurant")
    @ApiResponse(responseCode = "200", description = "Order preparation finished")
    public ResponseEntity<Order> finishOrderPreparation(
            @PathVariable Long restaurantId,
            @PathVariable Long orderId) {

        Order updatedOrder = orderService.finishOrderPreparation(orderId, restaurantId);
        return ResponseEntity.ok(updatedOrder);
    }

    @PostMapping("/{restaurantId}/menu/items")
    @Operation(summary = "Add item to menu", description = "Add a food item to the restaurant's menu")
    @ApiResponse(responseCode = "200", description = "Item added to menu")
    public ResponseEntity<Menu> addItemToMenu(
            @PathVariable Long restaurantId,
            @RequestParam Long itemId) {

        Menu updatedMenu = menuService.addItemToRestaurantMenu(restaurantId, itemId);
        return ResponseEntity.ok(updatedMenu);
    }

}