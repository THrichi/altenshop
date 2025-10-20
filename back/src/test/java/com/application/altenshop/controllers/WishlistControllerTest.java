package com.application.altenshop.controllers;

import com.application.altenshop.dtos.WishlistItemDTO;
import com.application.altenshop.services.WishlistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WishlistControllerTest {

    private WishlistController controller;

    @Mock
    private WishlistService wishlistService;

    @Mock
    private Authentication auth;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new WishlistController(wishlistService);
        when(auth.getName()).thenReturn("user@example.com");
    }

    @Test
    void testGetWishlist() {
        WishlistItemDTO item = new WishlistItemDTO(1L, "Ryzen 9", "img.png", "Component", 499.99);
        when(wishlistService.getWishlist("user@example.com")).thenReturn(List.of(item));

        ResponseEntity<List<WishlistItemDTO>> response = controller.getWishlist(auth);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals("Ryzen 9", response.getBody().get(0).productName());
        verify(wishlistService).getWishlist("user@example.com");
    }

    @Test
    void testAddToWishlist() {
        WishlistItemDTO item = new WishlistItemDTO(1L, "Ryzen 9", "img.png", "Component", 499.99);
        when(wishlistService.addToWishlist("user@example.com", 1L)).thenReturn(item);

        ResponseEntity<WishlistItemDTO> response = controller.add(1L, auth);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Ryzen 9", response.getBody().productName());
        verify(wishlistService).addToWishlist("user@example.com", 1L);
    }

    @Test
    void testRemoveFromWishlist() {
        doNothing().when(wishlistService).removeFromWishlist("user@example.com", 1L);

        ResponseEntity<Void> response = controller.remove(1L, auth);

        assertEquals(204, response.getStatusCodeValue());
        verify(wishlistService).removeFromWishlist("user@example.com", 1L);
    }
}
