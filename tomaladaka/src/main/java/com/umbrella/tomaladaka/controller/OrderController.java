package com.umbrella.tomaladaka.controller;

import com.umbrella.tomaladaka.dto.OrderRequest;
import com.umbrella.tomaladaka.model.*;
import com.umbrella.tomaladaka.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

  private final OrderService orderService;

  public OrderController(OrderService orderService) {
    this.orderService = orderService;
  }

  @PostMapping
  public ResponseEntity<Order> createOrder(@RequestBody OrderRequest request) {
    Order order = new Order(
      request.getClient(),
      request.getRestaurant(),
      request.getPaymentMethod(),
      request.getCart(),
      request.getOriginAddress(),
      request.getDestinationAddress()
    );

    Order created = orderService.createOrder(order);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @GetMapping
  public ResponseEntity<List<Order>> listOrders(@RequestParam(required = false) Status status) {
    List<Order> orders = (status == null)
    ? orderService.listOrders()
    : orderService.listOrdersByStatus(status);

    return ResponseEntity.ok(orders);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
    Order order = orderService.getOrderById(id);
    return ResponseEntity.ok(order);
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<Order> updateStatus(@PathVariable Long id, @RequestParam Status newStatus) {
    Order updated = orderService.updateStatus(id, newStatus);
    return ResponseEntity.ok(updated);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
    orderService.deleteOrder(id);
    return ResponseEntity.noContent().build();
  }
}

