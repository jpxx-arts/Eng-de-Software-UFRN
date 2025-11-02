package com.umbrella.tomaladaka.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "restaurants")
public class Restaurant {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  @Embedded
  private Address address;

  private String phone;

  public Restaurant(String name, Address address, String phone) {
    this.name = name;
    this.address = address;
    this.phone = phone;
  }

  public Restaurant(String name) {
    this.name = name;
  }
}

