package com.umbrella.tomaladaka.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umbrella.tomaladaka.dto.OrderRequest;
import com.umbrella.tomaladaka.model.*;
import com.umbrella.tomaladaka.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
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
  new Address("Rua A", "SP", "SP", "01234-567", "Brasil"),
  new Address("Rua B", "SP", "SP", "01235-678", "Brasil"));

  Order order2 = new Order(new User("Bob"), new Restaurant("Burger Place"), PaymentMethod.CASH,
  new Address("Rua C", "SP", "SP", "01236-789", "Brasil"),
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
  new Address("Rua A", "SP", "SP", "01234-567", "Brasil"),
  new Address("Rua B", "SP", "SP", "01235-678", "Brasil"));

  when(orderService.getOrderById(1L)).thenReturn(order);

  mockMvc.perform(get("/orders/1"))
  .andExpect(status().isOk())
  .andExpect(jsonPath("$.client.name").value("Alice"))
  .andExpect(jsonPath("$.restaurant.name").value("Pizza Place"));
  }

  @Test
  void testCreateOrder() throws Exception {
    OrderRequest requestDTO = new OrderRequest();
    requestDTO.setClientId(1L);
    requestDTO.setRestaurantId(1L);
    requestDTO.setPaymentMethod(PaymentMethod.CREDIT_CARD);
    requestDTO.setCart(new Cart());
    requestDTO.setOriginAddress(new Address("Rua A", "SP", "SP", "01234-567", "Brasil"));
    requestDTO.setDestinationAddress(new Address("Rua B", "SP", "SP", "01235-678", "Brasil"));

  Order orderToReturn = new Order(new User("Alice"), new Restaurant("Pizza Place"), PaymentMethod.CREDIT_CARD,
  new Address("Rua A", "SP", "SP", "01234-567", "Brasil"),
  new Address("Rua B", "SP", "SP", "01235-678", "Brasil"));

  when(orderService.createOrderFromRequest(any(OrderRequest.class))).thenReturn(orderToReturn);

  mockMvc.perform(post("/orders")
  .contentType(MediaType.APPLICATION_JSON)
  .content(objectMapper.writeValueAsString(requestDTO)))
  .andExpect(status().isCreated())
  .andExpect(jsonPath("$.client.name").value("Alice"))
  .andExpect(jsonPath("$.restaurant.name").value("Pizza Place"));
  }

  @Test
  void testUpdateStatus() throws Exception {
  Order order = new Order(new User("Alice"), new Restaurant("Pizza Place"), PaymentMethod.CREDIT_CARD,
  new Address("Rua A", "SP", "SP", "01234-567", "Brasil"),
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