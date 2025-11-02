package com.umbrella.tomaladaka.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;
import java.util.ArrayList;

@Data
@Entity
@Table(name = "orders")
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "client_id")
  private User client;

  @ManyToOne
  @JoinColumn(name = "restaurant_id")
  private Restaurant restaurant;

  @Enumerated(EnumType.STRING)
  private PaymentMethod paymentMethod;

  @Enumerated(EnumType.STRING)
  private Status status = Status.PENDING;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "order_id")
  private List<Item> items;

  @Embedded
  @AttributeOverrides({
    @AttributeOverride(name = "street", column = @Column(name = "origin_street")),
    @AttributeOverride(name = "city", column = @Column(name = "origin_city")),
    @AttributeOverride(name = "state", column = @Column(name = "origin_state")),
    @AttributeOverride(name = "zipCode", column = @Column(name = "origin_zip")),
    @AttributeOverride(name = "country", column = @Column(name = "origin_country")) // <-- ADICIONE ESTA LINHA
  })
  private Address originAddress;

  @Embedded
  @AttributeOverrides({
    @AttributeOverride(name = "street", column = @Column(name = "destination_street")),
    @AttributeOverride(name = "city", column = @Column(name = "destination_city")),
    @AttributeOverride(name = "state", column = @Column(name = "destination_state")),
    @AttributeOverride(name = "zipCode", column = @Column(name = "destination_zip")),
    @AttributeOverride(name = "country", column = @Column(name = "destination_country")) // <-- ADICIONE ESTA LINHA
  })
  private Address destinationAddress;

  public Order(User client, Restaurant restaurant, PaymentMethod paymentMethod,
    Cart cart, Address originAddress, Address destinationAddress) {
    this.client = client;
    this.restaurant = restaurant;
    this.paymentMethod = paymentMethod;
    this.status = Status.PENDING;

    this.items = new ArrayList<>(cart.getCartItems()); // <-- Mudança aqui

    this.originAddress = originAddress;
    this.destinationAddress = destinationAddress;
  }

  public Order() {}
}
