package com.application.altenshop.controllers;

import com.application.altenshop.dtos.CartItemDTO;
import com.application.altenshop.services.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartControllerTest {

    private CartController controller;

    @Mock
    private CartService cartService;

    @Mock
    private Authentication auth;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new CartController(cartService);
        when(auth.getName()).thenReturn("user@example.com");
    }

    @Test
    void testGetCart() {
        CartItemDTO item = new CartItemDTO(1L, "CPU", "desc", "img.png",
                99.99, 2, 10, 199.98);
        when(cartService.getCart("user@example.com")).thenReturn(List.of(item));

        ResponseEntity<List<CartItemDTO>> response = controller.getCart(auth);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(cartService).getCart("user@example.com");
    }

    @Test
    void testAddToCart() {
        CartItemDTO item = new CartItemDTO(1L, "CPU", "desc", "img.png",
                99.99, 1, 10, 99.99);
        when(cartService.addToCart("user@example.com", 1L, 1)).thenReturn(item);

        ResponseEntity<CartItemDTO> response = controller.addToCart(1L, 1, auth);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("CPU", response.getBody().productName());
        verify(cartService).addToCart("user@example.com", 1L, 1);
    }

    @Test
    void testUpdateQuantity() {
        CartItemDTO updated = new CartItemDTO(1L, "CPU", "desc", "img.png",
                99.99, 3, 10, 299.97);
        when(cartService.updateQuantity("user@example.com", 1L, 3)).thenReturn(updated);

        ResponseEntity<CartItemDTO> response = controller.updateQuantity(1L, 3, auth);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(3, response.getBody().quantity());
        verify(cartService).updateQuantity("user@example.com", 1L, 3);
    }

    @Test
    void testRemoveFromCart() {
        doNothing().when(cartService).removeFromCart("user@example.com", 1L);

        ResponseEntity<Void> response = controller.remove(1L, auth);

        assertEquals(204, response.getStatusCodeValue());
        verify(cartService).removeFromCart("user@example.com", 1L);
    }

    @Test
    void testClearCart() {
        doNothing().when(cartService).clearCart("user@example.com");

        ResponseEntity<Void> response = controller.clear(auth);

        assertEquals(204, response.getStatusCodeValue());
        verify(cartService).clearCart("user@example.com");
    }
}
