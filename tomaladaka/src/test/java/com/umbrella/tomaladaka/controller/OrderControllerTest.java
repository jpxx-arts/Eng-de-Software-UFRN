package com.umbrella.tomaladaka.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umbrella.tomaladaka.model.*;
import com.umbrella.tomaladaka.service.OrderService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private OrderService orderService;

  @Test
  void testListOrders() throws Exception {
    Cart cart = new Cart();

    Order order1 = Order.builder()
      .client(new User("Alice"))
      .restaurant(new Restaurant("Pizza Place"))
      .paymentMethod(PaymentMethod.CREDIT_CARD)
      .totalPrice(cart.getPrice())
      .items(cart.getCartItems())
      .originAddress(new Address("Rua A", "SP", "SP", "01234-567", "Brasil",-5.123, -35.123))
      .destinationAddress(new Address("Rua B", "SP", "SP", "01235-678", "Brasil", -5.12345, -35.132))
      .build();

    Order order2 = Order.builder()
      .client(new User("Bob"))
      .restaurant(new Restaurant("Burger Place"))
      .paymentMethod(PaymentMethod.CASH)
      .totalPrice(cart.getPrice())
      .items(cart.getCartItems())
      .originAddress(new Address("Rua C", "SP", "SP", "01236-567", "Brasil",-5.623, -35.523))
      .destinationAddress(new Address("Rua D", "SP", "SP", "01237-678", "Brasil", -5.72345, -32.432))
      .build();

    List<Order> orders = Arrays.asList(order1, order2);

    when(orderService.listOrders()).thenReturn(orders);

    mockMvc.perform(get("/orders"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(2))
      .andExpect(jsonPath("$[0].client.name").value("Alice"))
      .andExpect(jsonPath("$[1].client.name").value("Bob"));
  }

  @Test
  void testGetOrderById() throws Exception {

    Cart cart = new Cart();

    Order order = Order.builder()
      .client(new User("Alice"))
      .restaurant(new Restaurant("Pizza Place"))
      .paymentMethod(PaymentMethod.CREDIT_CARD)
      .totalPrice(cart.getPrice())
      .items(cart.getCartItems())
      .originAddress(new Address("Rua A", "SP", "SP", "01234-567", "Brasil",-5.623, -35.523))
      .destinationAddress(new Address("Rua B", "SP", "SP", "01235-678", "Brasil", -5.72345, -32.432))
      .build();

    when(orderService.getOrderById(1L)).thenReturn(order);

    mockMvc.perform(get("/orders/1"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.client.name").value("Alice"))
      .andExpect(jsonPath("$.restaurant.name").value("Pizza Place"));
  }

  @Test
  void testCreateOrder() throws Exception {
    Cart cart = new Cart();

    User user = new User("Alice");
    Restaurant restaurant = new Restaurant("Pizza Place");

    Address origin = new Address("Rua A", "SP", "SP", "01234-567", "Brasil",-5.623, -35.523);
    Address destination = new Address("Rua B", "SP", "SP", "01235-678", "Brasil", -5.72345, -32.432);


    Order orderToReturn = Order.builder()
      .client(user)
      .restaurant(restaurant)
      .paymentMethod(PaymentMethod.CREDIT_CARD)
      .totalPrice(cart.getPrice())
      .items(cart.getCartItems())
      .originAddress(origin)
      .destinationAddress(destination)
      .build();

    Map<String, Object> requestPayload = new HashMap<>();
    requestPayload.put("client", user);
    requestPayload.put("restaurant", restaurant);
    requestPayload.put("paymentMethod", PaymentMethod.CREDIT_CARD);
    requestPayload.put("cart", cart);
    requestPayload.put("originAddress", origin);
    requestPayload.put("destinationAddress", destination);

    when(orderService.createOrder(ArgumentMatchers.any(Order.class))).thenReturn(orderToReturn);

    mockMvc.perform(post("/orders")
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(requestPayload)))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.client.name").value("Alice"))
      .andExpect(jsonPath("$.restaurant.name").value("Pizza Place"));
  }

  @Test
  void testUpdateStatus() throws Exception {
    Cart cart = new Cart();

    Order order = Order.builder()
      .client(new User("Alice"))
      .restaurant(new Restaurant("Pizza Place"))
      .paymentMethod(PaymentMethod.CREDIT_CARD)
      .totalPrice(cart.getPrice())
      .items(cart.getCartItems())
      .originAddress(new Address("Rua A", "SP", "SP", "01234-567", "Brasil",-5.623, -35.523))
      .destinationAddress(new Address("Rua B", "SP", "SP", "01235-678", "Brasil", -5.72345, -32.432))
      .build();

    order.setStatus(Status.COMPLETED);

    when(orderService.updateStatus(1L, Status.COMPLETED)).thenReturn(order);

    mockMvc.perform(patch("/orders/1/status")
      .param("newStatus", "COMPLETED"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.status").value("COMPLETED"));
  }

  @Test
  void testDeleteOrder() throws Exception {
    doNothing().when(orderService).deleteOrder(1L);

    mockMvc.perform(delete("/orders/1"))
      .andExpect(status().isNoContent());

    verify(orderService, times(1)).deleteOrder(1L);
  }
}

