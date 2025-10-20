package com.application.altenshop.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JWTServiceTest {

    @InjectMocks
    private JWTService jwtService;

    private String secretBase64;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Génère une clé secrète valide (256 bits)
        SecretKey key = io.jsonwebtoken.security.Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
        secretBase64 = Base64.getEncoder().encodeToString(key.getEncoded());
        ReflectionTestUtils.setField(jwtService, "secretkey", secretBase64);

        userDetails = new User("user@example.com", "password", Collections.emptyList());
    }

    @Test
    void shouldGenerateAndValidateTokenSuccessfully() {
        String token = jwtService.generateToken(userDetails.getUsername());

        assertNotNull(token, "Token should not be null");
        assertTrue(jwtService.validateToken(token, userDetails), "Token should be valid");
        assertEquals("user@example.com", jwtService.extractEmail(token), "Extracted email should match username");
    }

    @Test
    void shouldDetectExpiredToken() {
        String expiredToken = Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis() - 100000))
                .expiration(new Date(System.currentTimeMillis() - 50000)) // already expired
                .signWith(jwtServiceTestKey())
                .compact();

        ReflectionTestUtils.setField(jwtService, "secretkey", secretBase64);
        assertFalse(jwtService.validateToken(expiredToken), "Expired token should be invalid");
    }


    @Test
    void shouldReturnFalseWhenUsernameMismatch() {
        String token = jwtService.generateToken("other@example.com");
        assertFalse(jwtService.validateToken(token, userDetails), "Token should be invalid when usernames differ");
    }

    @Test
    void shouldExtractEmailFromValidToken() {
        String token = jwtService.generateToken("john@alten.com");
        String email = jwtService.extractEmail(token);
        assertEquals("john@alten.com", email, "Extracted email must match");
    }

    // Helper to reuse same key
    private SecretKey jwtServiceTestKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secretBase64);
        return io.jsonwebtoken.security.Keys.hmacShaKeyFor(keyBytes);
    }
}
