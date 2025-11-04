package com.umbrella.tomaladaka.repository;

import com.umbrella.tomaladaka.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}