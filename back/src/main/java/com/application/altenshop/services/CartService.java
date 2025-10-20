package com.application.altenshop.services;

import com.application.altenshop.dtos.CartItemDTO;
import java.util.List;

public interface CartService {

    List<CartItemDTO> getCart(String email);

    CartItemDTO addToCart(String email, Long productId, int quantity);

    void removeFromCart(String email, Long productId);

    void clearCart(String email);

    CartItemDTO updateQuantity(String email, Long productId, int quantity);
}
