package com.application.altenshop.exceptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void testHandleResourceNotFound() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Product not found");
        ResponseEntity<String> response = handler.handleResourceNotFound(ex);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Product not found", response.getBody());
    }

    @Test
    void testHandleSecurityException() {
        SecurityException ex = new SecurityException("Access denied");
        ResponseEntity<String> response = handler.handleSecurity(ex);

        assertEquals(403, response.getStatusCodeValue());
        assertEquals("Access denied", response.getBody());
    }

    @Test
    void testHandleRuntimeException() {
        RuntimeException ex = new RuntimeException("Unexpected error");
        ResponseEntity<String> response = handler.handleRuntime(ex);

        assertEquals(403, response.getStatusCodeValue());
        assertEquals("Unexpected error", response.getBody());
    }
}
