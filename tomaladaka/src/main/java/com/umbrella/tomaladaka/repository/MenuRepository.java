package com.umbrella.tomaladaka.repository;

import com.umbrella.tomaladaka.model.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Long> {
}