package com.umbrella.tomaladaka.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umbrella.tomaladaka.model.Restaurant;
import com.umbrella.tomaladaka.repository.RestaurantRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RestaurantController.class)
class RestaurantControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private RestaurantRepository restaurantRepository;

  @Test
  void testCreateRestaurant() throws Exception {
    Restaurant restaurantToCreate = new Restaurant("Restaurante de Teste");

    Restaurant savedRestaurant = new Restaurant("Restaurante de Teste");
    savedRestaurant.setId(1L);

    when(restaurantRepository.save(ArgumentMatchers.any(Restaurant.class))).thenReturn(savedRestaurant);

    mockMvc.perform(post("/restaurants")
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(restaurantToCreate)))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.id").value(1L))
      .andExpect(jsonPath("$.name").value("Restaurante de Teste"));
  }
}
