package com.application.altenshop.controllers;

import com.application.altenshop.dtos.*;
import com.application.altenshop.models.User;
import com.application.altenshop.services.AuthService;
import com.application.altenshop.services.impl.AuthServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;


    @PostMapping("/account")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserRegisterDTO dto) {
        log.info("Registering new user: {}", dto.email());
        User savedUser = authService.register(dto);
        // on ne renvoie jamais l’entité directement (User) → toujours un DTO
        UserResponseDTO response = new UserResponseDTO(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getFirstname(),
                savedUser.getEmail()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/token")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginDTO dto) {
        log.info("User login attempt: {}", dto.email());
        // lookup séparé du login → utile si besoin de log ou vérif supplémentaire
        User user = authService.findByEmail(dto.email());
        String token = authService.login(dto.email(), dto.password());
        LoginResponseDTO response = new LoginResponseDTO(
                token,
                new UserResponseDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getFirstname(),
                        user.getEmail()
                )
        );
        return ResponseEntity.ok(response);
    }
}
