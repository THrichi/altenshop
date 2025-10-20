package com.application.altenshop.services;

import com.application.altenshop.dtos.UserRegisterDTO;
import com.application.altenshop.models.User;
import com.application.altenshop.repositories.UserRepository;
import com.application.altenshop.security.JWTService;
import com.application.altenshop.services.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JWTService jwtService;
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtService = mock(JWTService.class);
        authService = new AuthServiceImpl(userRepository, passwordEncoder, jwtService);
    }

    // =======================
    // TEST REGISTER
    // =======================
    @Test
    void register_ShouldSaveUser_WhenEmailNotUsed() {
        UserRegisterDTO dto = new UserRegisterDTO("john", "John", "john@example.com", "1234");

        when(userRepository.findByEmail(dto.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(dto.password())).thenReturn("encodedPwd");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User savedUser = authService.register(dto);

        assertEquals("john", savedUser.getUsername());
        assertEquals("John", savedUser.getFirstname());
        assertEquals("john@example.com", savedUser.getEmail());
        assertEquals("encodedPwd", savedUser.getPassword());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_ShouldThrowException_WhenEmailAlreadyExists() {
        UserRegisterDTO dto = new UserRegisterDTO("john", "John", "john@example.com", "1234");
        when(userRepository.findByEmail(dto.email())).thenReturn(Optional.of(new User()));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.register(dto));
        assertEquals("Un compte existe déjà avec cet email.", ex.getMessage());

        verify(userRepository, never()).save(any());
    }

    // =======================
    // TEST LOGIN
    // =======================
    @Test
    void login_ShouldReturnToken_WhenCredentialsAreValid() {
        String email = "john@example.com";
        String password = "1234";
        User user = new User();
        user.setEmail(email);
        user.setPassword("encodedPwd");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, "encodedPwd")).thenReturn(true);
        when(jwtService.generateToken(email)).thenReturn("jwtToken123");

        String token = authService.login(email, password);

        assertEquals("jwtToken123", token);
        verify(jwtService).generateToken(email);
    }

    @Test
    void login_ShouldThrow_WhenUserNotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.login("unknown@example.com", "1234"));

        assertEquals("Utilisateur introuvable", ex.getMessage());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void login_ShouldThrow_WhenPasswordInvalid() {
        User user = new User();
        user.setEmail("john@example.com");
        user.setPassword("encodedPwd");

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPwd", "encodedPwd")).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.login("john@example.com", "wrongPwd"));

        assertEquals("Mot de passe invalide", ex.getMessage());
        verify(jwtService, never()).generateToken(any());
    }

    // =======================
    // TEST FIND BY EMAIL
    // =======================
    @Test
    void findByEmail_ShouldReturnUser_WhenExists() {
        User user = new User();
        user.setEmail("john@example.com");

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        User found = authService.findByEmail("john@example.com");
        assertEquals("john@example.com", found.getEmail());
    }

    @Test
    void findByEmail_ShouldThrow_WhenNotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.findByEmail("unknown@example.com"));

        assertEquals("Utilisateur introuvable", ex.getMessage());
    }
}