package com.umbrella.tomaladaka.service;

import com.umbrella.tomaladaka.model.Address;
import com.umbrella.tomaladaka.model.Cart;
import com.umbrella.tomaladaka.model.PaymentMethod;
import com.umbrella.tomaladaka.model.Order;
import com.umbrella.tomaladaka.model.Restaurant;
import com.umbrella.tomaladaka.model.Status;
import com.umbrella.tomaladaka.model.User;
import com.umbrella.tomaladaka.repository.OrderRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OrderService {

  private final OrderRepository orderRepo;

  public OrderService(OrderRepository orderRepo) {
    this.orderRepo = orderRepo;
  }

  public Order createOrder(User client, Restaurant restaurant, PaymentMethod paymentMethod,
    Cart cart, Address originAddress, Address destinationAddress) {

    Order order = Order.builder()
      .client(client)
      .restaurant(restaurant)
      .paymentMethod(paymentMethod)
      .originAddress(originAddress)
      .destinationAddress(destinationAddress)
      .totalPrice(cart.getPrice())
      .items(cart.getCartItems())
      .build();
      
    return orderRepo.save(order);
  }

  public Order createOrder(Order order) {
    order.setStatus(Status.PENDING);
    return orderRepo.save(order);
  }

  public List<Order> listOrders() {
    return orderRepo.findAll();
  }

  public Order getOrderById(Long id) {
    return orderRepo.findById(id)
    .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));
  }

  public Order updateStatus(Long id, Status newStatus) {
    Order order = orderRepo.findById(id)
    .orElseThrow(() -> new RuntimeException("Order not found with id: " + id)); // Use sua exceção customizada

    Status currentStatus = order.getStatus();

    if (currentStatus == Status.COMPLETED || currentStatus == Status.CANCELLED) {
      throw new IllegalStateException("Cannot change status of a " + currentStatus + " order.");
    }

    if (newStatus == Status.PENDING && currentStatus != Status.PENDING) {
      throw new IllegalStateException("Cannot revert an order in progress back to PENDING.");
    }

    order.setStatus(newStatus);
    return orderRepo.save(order);
  }

  public void deleteOrder(Long id) {
    Order order = getOrderById(id);

    if (order.getStatus() == Status.COMPLETED) {
      throw new IllegalStateException("Completed orders cannot be deleted.");
    }

    if (order.getStatus() == Status.CANCELLED) {
      throw new IllegalStateException("Cancelled orders cannot be deleted (must be kept for history).");
    }

    orderRepo.delete(order);
  }

  public List<Order> listOrdersByStatus(Status status) {
    return orderRepo.findByStatus(status);
  }
}

