package com.application.altenshop.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testUserBuilderAndGetters() {
        User user = User.builder()
                .id(1L)
                .username("john")
                .firstname("John")
                .email("john@example.com")
                .password("secret")
                .build();

        assertEquals(1L, user.getId());
        assertEquals("john", user.getUsername());
        assertEquals("John", user.getFirstname());
        assertEquals("john@example.com", user.getEmail());
        assertEquals("secret", user.getPassword());
    }

    @Test
    void testUserEqualsAndHashCode() {
        User u1 = new User(1L, "john", "John", "john@example.com", "pwd");
        User u2 = new User(1L, "john", "John", "john@example.com", "pwd");

        assertEquals(u1, u2);
        assertEquals(u1.hashCode(), u2.hashCode());
    }
}