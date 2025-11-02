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
    Order order1 = new Order(new User("Alice"), new Restaurant("Pizza Place"), PaymentMethod.CREDIT_CARD,
      new Cart(), new Address("Rua A", "SP", "SP", "01234-567", "Brasil"),
      new Address("Rua B", "SP", "SP", "01235-678", "Brasil"));

    Order order2 = new Order(new User("Bob"), new Restaurant("Burger Place"), PaymentMethod.CASH,
      new Cart(), new Address("Rua C", "SP", "SP", "01236-789", "Brasil"),
      new Address("Rua D", "SP", "SP", "01237-890", "Brasil"));

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
    Order order = new Order(new User("Alice"), new Restaurant("Pizza Place"), PaymentMethod.CREDIT_CARD,
      new Cart(), new Address("Rua A", "SP", "SP", "01234-567", "Brasil"),
      new Address("Rua B", "SP", "SP", "01235-678", "Brasil"));

    when(orderService.getOrderById(1L)).thenReturn(order);

    mockMvc.perform(get("/orders/1"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.client.name").value("Alice"))
      .andExpect(jsonPath("$.restaurant.name").value("Pizza Place"));
  }

  @Test
  void testCreateOrder() throws Exception {
    Order orderToReturn = new Order(new User("Alice"), new Restaurant("Pizza Place"), PaymentMethod.CREDIT_CARD,
      new Cart(), new Address("Rua A", "SP", "SP", "01234-567", "Brasil"),
      new Address("Rua B", "SP", "SP", "01235-678", "Brasil"));

    Map<String, Object> requestPayload = new HashMap<>();
    requestPayload.put("client", new User("Alice"));
    requestPayload.put("restaurant", new Restaurant("Pizza Place"));
    requestPayload.put("paymentMethod", PaymentMethod.CREDIT_CARD);
    requestPayload.put("cart", new Cart()); // <-- O campo 'cart' que seu controller espera
    requestPayload.put("originAddress", new Address("Rua A", "SP", "SP", "01234-567", "Brasil"));
    requestPayload.put("destinationAddress", new Address("Rua B", "SP", "SP", "01235-678", "Brasil"));

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
    Order order = new Order(new User("Alice"), new Restaurant("Pizza Place"), PaymentMethod.CREDIT_CARD,
      new Cart(), new Address("Rua A", "SP", "SP", "01234-567", "Brasil"),
      new Address("Rua B", "SP", "SP", "01235-678", "Brasil"));
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

