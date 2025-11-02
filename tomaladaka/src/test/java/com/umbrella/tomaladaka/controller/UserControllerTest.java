package com.umbrella.tomaladaka.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umbrella.tomaladaka.model.User;
import com.umbrella.tomaladaka.repository.UserRepository;
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

@WebMvcTest(UserController.class)
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private UserRepository userRepository;

  @Test
  void testCreateUser() throws Exception {
    User userToCreate = new User("Cliente de Teste");
    userToCreate.setEmail("teste@cliente.com"); 

    User savedUser = new User("Cliente de Teste");
    savedUser.setEmail("teste@cliente.com");
    savedUser.setId(1L);

    when(userRepository.save(ArgumentMatchers.any(User.class))).thenReturn(savedUser);

    mockMvc.perform(post("/users")
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(userToCreate)))
      .andExpect(status().isCreated()) // Espera 201 CREATED
      .andExpect(jsonPath("$.id").value(1L))
      .andExpect(jsonPath("$.name").value("Cliente de Teste"))
      .andExpect(jsonPath("$.email").value("teste@cliente.com"));
  }
}
