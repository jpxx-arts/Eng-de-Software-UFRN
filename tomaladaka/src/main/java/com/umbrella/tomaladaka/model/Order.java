package com.umbrella.tomaladaka.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@Table(name = "orders")
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
  @Builder.Default
  private Status status = Status.PENDING;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "order_id")
  private List<Item> items;

  @Column(nullable = false)
  private Double totalPrice;

  @Embedded
  @AttributeOverrides({
    @AttributeOverride(name = "street", column = @Column(name = "origin_street")),
    @AttributeOverride(name = "city", column = @Column(name = "origin_city")),
    @AttributeOverride(name = "state", column = @Column(name = "origin_state")),
    @AttributeOverride(name = "zipCode", column = @Column(name = "origin_zip")),
    @AttributeOverride(name = "country", column = @Column(name = "origin_country")),
    @AttributeOverride(name = "latitude", column = @Column(name = "origin_latitude")),
    @AttributeOverride(name = "longitude", column = @Column(name = "origin_longitude"))
  })
  private Address originAddress;

  @Embedded
  @AttributeOverrides({
    @AttributeOverride(name = "street", column = @Column(name = "destination_street")),
    @AttributeOverride(name = "city", column = @Column(name = "destination_city")),
    @AttributeOverride(name = "state", column = @Column(name = "destination_state")),
    @AttributeOverride(name = "zipCode", column = @Column(name = "destination_zip")),
    @AttributeOverride(name = "country", column = @Column(name = "destination_country")),
    @AttributeOverride(name = "latitude", column = @Column(name = "destination_latitude")),
    @AttributeOverride(name = "longitude", column = @Column(name = "destination_longitude"))
  })
  private Address destinationAddress;

  @Transient
  public int getEstimatedDeliveryTime() {
    int itemsPreparationTime = items.stream().mapToInt(Item::getPreparationTime).sum();

    final double meanVelocity = 0.71; // km/min
    
    int deliveryTime = ((int)(this.getDeliveryDistance(originAddress, destinationAddress)/meanVelocity));

    return deliveryTime + itemsPreparationTime;
  }

  private double getDeliveryDistance(Address origin, Address destination){
    double lat1Rad = Math.toRadians(origin.getLatitude());
    double lat2Rad = Math.toRadians(destination.getLatitude());
    double deltaLat = Math.toRadians(origin.getLatitude() - destination.getLatitude());
    double deltaLon = Math.toRadians(origin.getLongitude() - destination.getLongitude());

    final double RAIO_TERRA_KM = 6371;

    // a = sin²(Δlat/2) + cos(lat1) * cos(lat2) * sin²(Δlon/2)
    double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

    // c = 2 * atan2(√a, √(1−a))
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

    // d = R * c
    double distancia = RAIO_TERRA_KM * c;

    return distancia;
  }
}
