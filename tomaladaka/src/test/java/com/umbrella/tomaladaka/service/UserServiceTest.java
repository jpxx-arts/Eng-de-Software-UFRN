package com.umbrella.tomaladaka.service;

import com.umbrella.tomaladaka.dto.UserRequest;
import com.umbrella.tomaladaka.model.User;
import com.umbrella.tomaladaka.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepo;

    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepo);
    }

    @Test
    void createUser_shouldSaveAndReturnUser() {
        UserRequest req = new UserRequest("Teste", "engosft@ufrn.com");

        User saved = User.builder()
            .id(1L)
            .name(req.name())
            .email(req.email())
            .build();

        when(userRepo.save(any(User.class))).thenReturn(saved);

        User result = userService.createUser(req);

        assertNotNull(result);
        assertEquals("Teste", result.getName());
        assertEquals("engosft@ufrn.com", result.getEmail());
        verify(userRepo, times(1)).save(any(User.class));
    }

    @Test
    void getUserById_shouldReturnUser_whenFound() {
        User existing = User.builder().id(1L).name("Prof").email("a@a.com").build();
        when(userRepo.findById(1L)).thenReturn(Optional.of(existing));

        User result = userService.getUserById(1L);

        assertEquals("Prof", result.getName());
        assertEquals("a@a.com", result.getEmail());
    }

    @Test
    void getUserById_shouldThrow_whenNotFound() {
        when(userRepo.findById(2L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.getUserById(2L));
    }

    @Test
    void getAllUsers_shouldReturnList() {
        List<User> users = Arrays.asList(
            User.builder().id(1L).name("A").email("a@a").build(),
            User.builder().id(2L).name("B").email("b@b").build()
        );

        when(userRepo.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
        verify(userRepo, times(1)).findAll();
    }

    @Test
    void updateUser_shouldModifyAndSave() {
        User existing = User.builder().id(1L).name("Prof").email("prof@ufrn.com").build();
        User details = User.builder().name("Prof2").email("prof2@ufrn.com").build();

        when(userRepo.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepo.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updated = userService.updateUser(1L, details);

        assertEquals("Prof2", updated.getName());
        assertEquals("prof2@ufrn.com", updated.getEmail());
        verify(userRepo, times(1)).save(existing);
    }

    @Test
    void deleteUser_shouldCallDeleteOnRepo() {
        User existing = User.builder().id(1L).name("aluno").email("aluno@ufrn.com").build();
        when(userRepo.findById(1L)).thenReturn(Optional.of(existing));

        userService.deleteUser(1L);

        verify(userRepo, times(1)).delete(existing);
    }

    @Test
    void updateUser_shouldUpdateOnlyNameWhenEmailNull() {
        User existing = User.builder().id(1L).name("aluno").email("aluno@ufrn.com").build();
        User details = User.builder().name("aluno2").build();

        when(userRepo.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepo.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updated = userService.updateUser(1L, details);

        assertEquals("aluno2", updated.getName());
        assertEquals("aluno@ufrn.com", updated.getEmail());
        verify(userRepo, times(1)).save(existing);
    }

    @Test
    void updateUser_shouldUpdateOnlyEmailWhenNameNull() {
        User existing = User.builder().id(1L).name("prof").email("prof@ufrn.com").build();
        User details = User.builder().email("prof2@ufrn.com").build();

        when(userRepo.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepo.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updated = userService.updateUser(1L, details);

        assertEquals("prof", updated.getName());
        assertEquals("prof2@ufrn.com", updated.getEmail());
        verify(userRepo, times(1)).save(existing);
    }

    @Test
    void updateUser_shouldNotChangeWhenDetailsFieldsAreNull() {
        User existing = User.builder().id(1L).name("prof").email("prof@ufrn.com").build();
        User details = User.builder().build();

        when(userRepo.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepo.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updated = userService.updateUser(1L, details);

        assertEquals("prof", updated.getName());
        assertEquals("prof@ufrn.com", updated.getEmail());
        verify(userRepo, times(1)).save(existing);
    }
}
