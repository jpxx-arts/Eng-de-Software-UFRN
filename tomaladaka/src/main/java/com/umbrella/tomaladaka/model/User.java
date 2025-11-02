package com.umbrella.tomaladaka.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  private String email;

  public User(String name, String email) {
    this.name = name;
    this.email = email;
  }

  public User(String name) {
    this.name = name;
  }
}

