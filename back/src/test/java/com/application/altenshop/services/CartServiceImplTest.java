package com.application.altenshop.services;

import com.application.altenshop.dtos.CartItemDTO;
import com.application.altenshop.exceptions.ResourceNotFoundException;
import com.application.altenshop.models.*;
import com.application.altenshop.repositories.*;
import com.application.altenshop.services.impl.CartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartServiceImplTest {

    private CartItemRepository cartRepo;
    private ProductRepository productRepo;
    private UserRepository userRepo;
    private CartServiceImpl cartService;

    private final User user = new User(1L, "john", "John", "john@example.com", "pwd");
    private final Product product = new Product(
            1L, "CODE", "Laptop", "desc", "img", "cat",
            1000.0, 10, "INT", 1L,InventoryStatus.INSTOCK , 5, 0L, 0L
    );
    private CartItem item;

    @BeforeEach
    void setUp() {
        cartRepo = mock(CartItemRepository.class);
        productRepo = mock(ProductRepository.class);
        userRepo = mock(UserRepository.class);
        cartService = new CartServiceImpl(cartRepo, productRepo, userRepo);
        item = CartItem.builder().user(user).product(product).quantity(2).build();
    }

    // ========================
    // getCart
    // ========================
    @Test
    void getCart_ShouldReturnCartItems_WhenUserExists() {
        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(cartRepo.findByUser(user)).thenReturn(List.of(item));

        List<CartItemDTO> result = cartService.getCart(user.getEmail());

        assertEquals(1, result.size());
        assertEquals("Laptop", result.get(0).productName());
        assertEquals(2000.0, result.get(0).total());
    }

    @Test
    void getCart_ShouldThrow_WhenUserNotFound() {
        when(userRepo.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cartService.getCart("unknown@example.com"));
    }

    // ========================
    // addToCart
    // ========================
    @Test
    void addToCart_ShouldCreateNewItem_WhenNotExisting() {
        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(productRepo.findById(1L)).thenReturn(Optional.of(product));
        when(cartRepo.findByUserAndProductId(user, 1L)).thenReturn(Optional.empty());
        when(cartRepo.save(any(CartItem.class))).thenAnswer(i -> i.getArgument(0));

        CartItemDTO dto = cartService.addToCart(user.getEmail(), 1L, 3);

        assertEquals(3, dto.quantity());
        assertEquals(3000.0, dto.total());
    }

    @Test
    void addToCart_ShouldIncrementQuantity_WhenAlreadyExists() {
        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(productRepo.findById(1L)).thenReturn(Optional.of(product));
        when(cartRepo.findByUserAndProductId(user, 1L)).thenReturn(Optional.of(item));
        when(cartRepo.save(any(CartItem.class))).thenAnswer(i -> i.getArgument(0));

        CartItemDTO dto = cartService.addToCart(user.getEmail(), 1L, 1);

        assertEquals(3, dto.quantity());
    }

    @Test
    void addToCart_ShouldThrow_WhenUserNotFound() {
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> cartService.addToCart("x", 1L, 1));
    }

    @Test
    void addToCart_ShouldThrow_WhenProductNotFound() {
        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(productRepo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> cartService.addToCart(user.getEmail(), 99L, 1));
    }

    // ========================
    // removeFromCart
    // ========================
    @Test
    void removeFromCart_ShouldCallDelete_WhenUserFound() {
        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        cartService.removeFromCart(user.getEmail(), 1L);

        verify(cartRepo).deleteByUserAndProductId(user, 1L);
    }

    @Test
    void removeFromCart_ShouldThrow_WhenUserNotFound() {
        when(userRepo.findByEmail("x")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> cartService.removeFromCart("x", 1L));
    }

    // ========================
    // clearCart
    // ========================
    @Test
    void clearCart_ShouldDeleteAll_WhenUserExists() {
        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(cartRepo.findByUser(user)).thenReturn(List.of(item));

        cartService.clearCart(user.getEmail());

        verify(cartRepo).deleteAll(anyList());
    }

    @Test
    void clearCart_ShouldThrow_WhenUserNotFound() {
        when(userRepo.findByEmail("x")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> cartService.clearCart("x"));
    }

    // ========================
    // updateQuantity
    // ========================
    @Test
    void updateQuantity_ShouldUpdate_WhenItemExists() {
        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(cartRepo.findByUserAndProductId(user, 1L)).thenReturn(Optional.of(item));
        when(cartRepo.save(item)).thenReturn(item);

        CartItemDTO dto = cartService.updateQuantity(user.getEmail(), 1L, 5);

        assertEquals(5, dto.quantity());
        assertEquals(5000.0, dto.total());
    }

    @Test
    void updateQuantity_ShouldThrow_WhenItemNotFound() {
        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(cartRepo.findByUserAndProductId(user, 1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cartService.updateQuantity(user.getEmail(), 1L, 3));
    }
}
