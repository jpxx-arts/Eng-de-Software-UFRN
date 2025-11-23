package com.umbrella.tomaladaka.controller;

import com.umbrella.tomaladaka.dto.OrderRequest;
import com.umbrella.tomaladaka.model.*;
import com.umbrella.tomaladaka.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/orders")
@Tag(name = "Order Controller", description = "Manage customer orders")
public class OrderController {

  private final OrderService orderService;

  public OrderController(OrderService orderService) {
    this.orderService = orderService;
  }

  @PostMapping
  @Operation(summary = "Create a new order", description = "Place a new order")
  @ApiResponse(responseCode = "201", description = "Order created successfully")
  public ResponseEntity<Order> createOrder(@RequestBody OrderRequest request) {
    Order created = orderService.createOrderFromRequest(request);
    URI location = URI.create(String.format("/orders/%d", created.getId()));
    return ResponseEntity.created(location).body(created);
  }

  @GetMapping
  @Operation(summary = "List orders", description = "List all orders or filter by status")
  @ApiResponse(responseCode = "200", description = "List of orders retrieved")
  public ResponseEntity<List<Order>> listOrders(@RequestParam(required = false) Status status) {
    List<Order> orders = (status == null)
    ? orderService.listOrders()
    : orderService.listOrdersByStatus(status);

    return ResponseEntity.ok(orders);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get order by ID", description = "Retrieve a specific order by its ID")
  @ApiResponse(responseCode = "200", description = "Order found")
  @ApiResponse(responseCode = "404", description = "Order not found")
  public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
    Order order = orderService.getOrderById(id);
    return ResponseEntity.ok(order);
  }

  @PostMapping("/{id}/assign-delivery")
    @Operation(summary = "Assign delivery", description = "Assign a delivery person to an order")
    @ApiResponse(responseCode = "200", description = "Delivery assigned")
    public ResponseEntity<Order> assignDelivery(
            @PathVariable Long id,
            @RequestParam Long deliveryManId) {
        
        Order updatedOrder = orderService.assignDelivery(id, deliveryManId);
        return ResponseEntity.ok(updatedOrder);
  }

  @PatchMapping("/{id}/status")
  @Operation(summary = "Update order status", description = "Update the status of an order")
  @ApiResponse(responseCode = "200", description = "Status updated")
  public ResponseEntity<Order> updateStatus(@PathVariable Long id, @RequestParam Status newStatus) {
    Order updated = orderService.updateStatus(id, newStatus);
    return ResponseEntity.ok(updated);
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete order", description = "Delete an order by ID")
  @ApiResponse(responseCode = "204", description = "Order deleted")
  public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
    orderService.deleteOrder(id);
    return ResponseEntity.noContent().build();
  }
}

