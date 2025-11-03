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
import com.umbrella.tomaladaka.repository.RestaurantRepository;
import com.umbrella.tomaladaka.repository.UserRepository;

@Service
public class OrderService {

  private final OrderRepository orderRepo;
  private final RestaurantRepository restaurantRepo;
  private final UserRepository userRepo;

  public OrderService(OrderRepository orderRepo, RestaurantRepository restaurantRepo,
      UserRepository userRepo) {

    this.orderRepo = orderRepo;
    this.restaurantRepo = restaurantRepo;
    this.userRepo = userRepo;
  }

  public Order createOrder(User client, Restaurant restaurant, PaymentMethod paymentMethod,
      Cart cart, Address originAddress, Address destinationAddress) {

    Order order = new Order(client, restaurant, paymentMethod, cart, originAddress, destinationAddress);
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
        .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));

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

  public Order acceptOrder(Long orderId, Long restaurantId) {
    Order order = getOrderById(orderId);

    Restaurant restaurant = restaurantRepo.findById(restaurantId)
        .orElseThrow(() -> new IllegalArgumentException("Restaurant not found: " + restaurantId));

    if (!order.getRestaurant().getId().equals(restaurant.getId())) {
      throw new IllegalStateException("Order does not belong to this restaurant.");
    }

    if (order.getStatus() != Status.PENDING) {
      throw new IllegalStateException("Cannot accept an order that is not PENDING.");
    }

    order.setStatus(Status.PROCESSING);
    return orderRepo.save(order);
  }

  public Order finishOrderPreparation(Long orderId, Long restaurantId) {
    Order order = getOrderById(orderId);

    if (!order.getRestaurant().getId().equals(restaurantId)) {
      throw new IllegalStateException("Order does not belong to this restaurant.");
    }

    if (order.getStatus() != Status.PROCESSING) {
      throw new IllegalStateException("Cannot finish preparation for an order that is not PROCESSING.");
    }

    order.setStatus(Status.READY_FOR_DELIVERY);
    return orderRepo.save(order);
  }

  public Order assignDelivery(Long orderId, Long deliveryManId) {
    Order order = getOrderById(orderId);

    if (order.getStatus() != Status.READY_FOR_DELIVERY) {
      throw new IllegalStateException("Order is not ready for delivery.");
    }

    User deliveryMan = userRepo.findById(deliveryManId)
        .orElseThrow(() -> new IllegalArgumentException("Delivery person not found: " + deliveryManId));

    order.setDeliveryMan(deliveryMan);

    order.setStatus(Status.OUT_FOR_DELIVERY);
    return orderRepo.save(order);
  }
}
