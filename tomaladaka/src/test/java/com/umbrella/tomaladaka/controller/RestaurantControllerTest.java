package com.umbrella.tomaladaka.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umbrella.tomaladaka.dto.RestaurantRequest;
import com.umbrella.tomaladaka.model.Restaurant;
import com.umbrella.tomaladaka.service.RestaurantService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

@WebMvcTest(RestaurantController.class)
class RestaurantControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private RestaurantService restaurantService;

  @Test
  void testCreateRestaurant() throws Exception {
    Restaurant restaurant = Restaurant.builder()
    .name("Resturante de Teste")
    .phone("84996173514")
    .build();

    Map<String, Object> requestPayload = new HashMap<>();
    requestPayload.put("name", restaurant.getName());
    requestPayload.put("phone", restaurant.getPhone());

    when(restaurantService.createRestaurant(any(RestaurantRequest.class))).thenReturn(restaurant);

    mockMvc.perform(post("/restaurants")
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(requestPayload)))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.id").value(1L))
      .andExpect(jsonPath("$.name").value(restaurant.getName()));
  }
}
