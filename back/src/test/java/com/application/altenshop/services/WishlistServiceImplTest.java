package com.application.altenshop.services;

import com.application.altenshop.dtos.WishlistItemDTO;
import com.application.altenshop.exceptions.ResourceNotFoundException;
import com.application.altenshop.models.*;
import com.application.altenshop.repositories.*;
import com.application.altenshop.services.impl.WishlistServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WishlistServiceImplTest {

    private WishlistRepository wishlistRepo;
    private ProductRepository productRepo;
    private UserRepository userRepo;
    private WishlistServiceImpl service;

    private User user;
    private Product product;
    private WishlistItem item;

    @BeforeEach
    void setUp() {
        wishlistRepo = mock(WishlistRepository.class);
        productRepo = mock(ProductRepository.class);
        userRepo = mock(UserRepository.class);
        service = new WishlistServiceImpl(wishlistRepo, productRepo, userRepo);

        user = new User(1L, "john", "John", "john@example.com", "pwd");
        product = new Product(
                1L, "CODE", "Laptop", "desc", "img",
                "Component", 1000.0, 10, "INT", 1L,
                InventoryStatus.INSTOCK, 5, 0L, 0L
        );
        item = WishlistItem.builder().user(user).product(product).build();
    }

    // ========================
    // getWishlist()
    // ========================
    @Test
    void getWishlist_ShouldReturnList_WhenUserExists() {
        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(wishlistRepo.findByUser(user)).thenReturn(List.of(item));

        List<WishlistItemDTO> result = service.getWishlist(user.getEmail());

        assertEquals(1, result.size());
        assertEquals("Laptop", result.get(0).productName());
        assertEquals(1000.0, result.get(0).price());
    }

    @Test
    void getWishlist_ShouldThrow_WhenUserNotFound() {
        when(userRepo.findByEmail("x")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getWishlist("x"));
    }

    // ========================
    // addToWishlist()
    // ========================
    @Test
    void addToWishlist_ShouldAdd_WhenNotExists() {
        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(productRepo.findById(1L)).thenReturn(Optional.of(product));
        when(wishlistRepo.findByUserAndProductId(user, 1L)).thenReturn(Optional.empty());
        when(wishlistRepo.save(any(WishlistItem.class))).thenAnswer(i -> i.getArgument(0));

        WishlistItemDTO dto = service.addToWishlist(user.getEmail(), 1L);

        assertEquals("Laptop", dto.productName());
        assertEquals(1000.0, dto.price());
        verify(wishlistRepo).save(any(WishlistItem.class));
    }

    @Test
    void addToWishlist_ShouldNotDuplicate_WhenAlreadyExists() {
        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(productRepo.findById(1L)).thenReturn(Optional.of(product));
        when(wishlistRepo.findByUserAndProductId(user, 1L)).thenReturn(Optional.of(item));

        WishlistItemDTO dto = service.addToWishlist(user.getEmail(), 1L);

        assertEquals("Laptop", dto.productName());
        verify(wishlistRepo, never()).save(any());
    }

    @Test
    void addToWishlist_ShouldThrow_WhenUserNotFound() {
        when(userRepo.findByEmail("x")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.addToWishlist("x", 1L));
    }

    @Test
    void addToWishlist_ShouldThrow_WhenProductNotFound() {
        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(productRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.addToWishlist(user.getEmail(), 1L));
    }

    // ========================
    // removeFromWishlist()
    // ========================
    @Test
    void removeFromWishlist_ShouldDelete_WhenUserExists() {
        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        service.removeFromWishlist(user.getEmail(), 1L);

        verify(wishlistRepo).deleteByUserAndProductId(user, 1L);
    }

    @Test
    void removeFromWishlist_ShouldThrow_WhenUserNotFound() {
        when(userRepo.findByEmail("x")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.removeFromWishlist("x", 1L));
    }
}
