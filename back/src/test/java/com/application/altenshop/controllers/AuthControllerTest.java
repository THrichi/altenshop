package com.application.altenshop.controllers;

import com.application.altenshop.dtos.*;
import com.application.altenshop.models.User;
import com.application.altenshop.services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    private AuthController controller;

    @Mock
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new AuthController(authService);
    }

    @Test
    void testRegisterUser() {
        // Arrange
        UserRegisterDTO registerDTO = new UserRegisterDTO(
                "john_doe",
                "John",
                "john@example.com",
                "password123"
        );

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("john_doe");
        savedUser.setFirstname("John");
        savedUser.setEmail("john@example.com");

        when(authService.register(registerDTO)).thenReturn(savedUser);

        // Act
        ResponseEntity<UserResponseDTO> response = controller.register(registerDTO);

        // Assert
        assertEquals(201, response.getStatusCodeValue());
        assertEquals("john_doe", response.getBody().username());
        verify(authService).register(registerDTO);
    }

    @Test
    void testLoginUser() {
        // Arrange
        LoginDTO loginDTO = new LoginDTO("john@example.com", "password123");

        User user = new User();
        user.setId(1L);
        user.setUsername("john_doe");
        user.setFirstname("John");
        user.setEmail("john@example.com");

        when(authService.findByEmail("john@example.com")).thenReturn(user);
        when(authService.login("john@example.com", "password123")).thenReturn("fake-jwt-token");

        // Act
        ResponseEntity<LoginResponseDTO> response = controller.login(loginDTO);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("fake-jwt-token", response.getBody().token());
        assertEquals("john_doe", response.getBody().user().username());
        verify(authService).findByEmail("john@example.com");
        verify(authService).login("john@example.com", "password123");
    }
}
