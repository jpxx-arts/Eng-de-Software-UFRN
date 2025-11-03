package com.umbrella.tomaladaka.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
//import java.util.ArrayList;

import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToOne;

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

  @ElementCollection
  @CollectionTable(name = "restaurant_delivery_personnel",
                  joinColumns = @JoinColumn(name = "restaurant_id")
  )
  @Column(name = "delivery_person_name")
  private List<String> deliveryPersonnelNames;

  @OneToOne(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
  private Menu menu;

  public Restaurant(String name, Address address, String phone) {
    this.name = name;
    this.address = address;
    this.phone = phone;
    this.deliveryPersonnelNames = new ArrayList<>();
  }

  public Restaurant(String name) {
    this.name = name;
    this.deliveryPersonnelNames = new ArrayList<>();
  }

  public void addDeliveryPerson(String name) {
    if (this.deliveryPersonnelNames == null) {
      this.deliveryPersonnelNames = new ArrayList<>();
    }
    this.deliveryPersonnelNames.add(name);
  }

  public void removeDeliveryPerson(String name) {
    if (this.deliveryPersonnelNames != null) {
      this.deliveryPersonnelNames.remove(name);
    }
  }
}

