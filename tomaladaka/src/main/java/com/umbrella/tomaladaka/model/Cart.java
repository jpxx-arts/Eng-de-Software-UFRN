package com.umbrella.tomaladaka.model;
import java.util.List;

import lombok.Data;

import java.util.ArrayList;
import java.util.Iterator;

/*
* The cart is not being saved in the database,
* so we have to persist it on frontend using cookies
*/

@Data
public class Cart {
  private List<Item> cartItems;

  public Cart(List<Item> cartItems) {
    this.cartItems = cartItems;
  }

  public Cart() {
    this.cartItems = new ArrayList<>();
  }

  public Double getPrice() {
    Iterator<Item> items = cartItems.iterator();
    Double price = 0.;

    while (items.hasNext()) {
      price += items.next().getPrice();
    }

    return price;
  }

  public void addItem(Item item) {
    cartItems.add(item);
  }

  public void removeItem(Item item) {
    cartItems.remove(item);
  }

  public void clearItem() {
    cartItems.clear();
  }
}
