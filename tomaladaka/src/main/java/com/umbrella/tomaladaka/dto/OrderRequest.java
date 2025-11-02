package com.umbrella.tomaladaka.dto;

import com.umbrella.tomaladaka.model.*;

import lombok.Data;

@Data
public class OrderRequest {
  private User client;
  private Restaurant restaurant;
  private PaymentMethod paymentMethod;
  private Cart cart;
  private Address originAddress;
  private Address destinationAddress;
}
