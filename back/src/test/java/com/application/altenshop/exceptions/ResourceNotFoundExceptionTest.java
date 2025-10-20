package com.application.altenshop.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResourceNotFoundExceptionTest {

    @Test
    void testConstructorAndMessage() {
        String message = "User not found";
        ResourceNotFoundException ex = new ResourceNotFoundException(message);

        assertEquals(message, ex.getMessage());

        assertTrue(ex instanceof RuntimeException);
    }
}
