package com.umbrella.tomaladaka.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.umbrella.tomaladaka.model.Order;
import com.umbrella.tomaladaka.model.Status;

public interface OrderRepository extends JpaRepository<Order, Long> {
  List<Order> findByStatus(Status status);
}
