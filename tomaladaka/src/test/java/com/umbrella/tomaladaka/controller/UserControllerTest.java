package com.umbrella.tomaladaka.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umbrella.tomaladaka.TomaladakaApplication;
import com.umbrella.tomaladaka.dto.UserRequest;
import com.umbrella.tomaladaka.model.User;
import com.umbrella.tomaladaka.repository.UserRepository;
import com.umbrella.tomaladaka.service.UserService;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
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

@WebMvcTest(
  controllers = UserController.class,
  properties = "spring.main.allow-bean-definition-overriding=true"
)
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private UserService userService;

  @Test
  void testCreateUser() throws Exception {
    User userToReturn = User.builder()
      .name("Cliente de Teste")
      .email("teste@cliente.com")
      .build();

    Map<String, Object> requestPayload = new HashMap<>();

    requestPayload.put("name", userToReturn.getName());
    requestPayload.put("email", userToReturn.getEmail());

    when(userService.createUser(any(UserRequest.class))).thenReturn(userToReturn);

    mockMvc.perform(post("/users")
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(userToReturn)))
      .andExpect(status().isCreated()) // Espera 201 CREATED
      .andExpect(jsonPath("$.name").value("Cliente de Teste"))
      .andExpect(jsonPath("$.email").value("teste@cliente.com"));
  }
}
