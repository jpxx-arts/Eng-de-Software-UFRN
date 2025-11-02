package com.umbrella.tomaladaka.service;

import com.umbrella.tomaladaka.model.Cart;
import com.umbrella.tomaladaka.model.Item;
import org.springframework.stereotype.Service;

@Service
public class CartService {

  public void addItemToCart(Cart cart, Item item) {
    cart.addItem(item);
  }

  public void removeItemFromCart(Cart cart, Item item) {
    cart.removeItem(item);
  }

  public void clearCart(Cart cart) {
    cart.clearItem();
  }

  public Double getTotal(Cart cart) {
    return cart.getPrice();
  }
}

