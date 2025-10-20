package com.application.altenshop.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminGuardTest {

    private AdminGuard adminGuard;

    @BeforeEach
    void setUp() {
        adminGuard = new AdminGuard();
    }

    @Test
    void shouldAllowAdminUser() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("admin@admin.com");

        assertDoesNotThrow(() -> adminGuard.ensureAdmin(auth));
    }

    @Test
    void shouldRejectNullAuthentication() {
        assertThrows(SecurityException.class, () -> adminGuard.ensureAdmin(null));
    }

    @Test
    void shouldRejectWhenEmailIsNull() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(null);

        SecurityException ex = assertThrows(SecurityException.class, () -> adminGuard.ensureAdmin(auth));
        assertEquals("Only admin can perform this operation", ex.getMessage());
    }

    @Test
    void shouldRejectWhenEmailIsNotAdmin() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("user@example.com");

        assertThrows(SecurityException.class, () -> adminGuard.ensureAdmin(auth));
    }

    @Test
    void shouldIgnoreCaseInEmailCheck() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("ADMIN@ADMIN.COM");

        assertDoesNotThrow(() -> adminGuard.ensureAdmin(auth));
    }
}
