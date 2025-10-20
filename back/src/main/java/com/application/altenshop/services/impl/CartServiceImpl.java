package com.application.altenshop.services.impl;

import com.application.altenshop.dtos.CartItemDTO;
import com.application.altenshop.exceptions.ResourceNotFoundException;
import com.application.altenshop.models.*;
import com.application.altenshop.repositories.*;
import com.application.altenshop.services.CartService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartRepo;
    private final ProductRepository productRepo;
    private final UserRepository userRepo;

    public List<CartItemDTO> getCart(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

        // conversion entités → DTO pour renvoyer des infos produit + quantité + total
        return cartRepo.findByUser(user)
                .stream()
                .map(item -> new CartItemDTO(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getProduct().getDescription(),
                        item.getProduct().getImage(),
                        item.getProduct().getPrice(),
                        item.getQuantity(),
                        item.getProduct().getQuantity(),
                        item.getProduct().getPrice() * item.getQuantity()
                ))
                .toList();
    }

    public CartItemDTO addToCart(String email, Long productId, int quantity) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: id=" + productId));

        // si le produit est déjà dans le panier → on incrémente la quantité
        // sinon on crée un nouvel item
        CartItem item = cartRepo.findByUserAndProductId(user, productId)
                .map(existing -> {
                    existing.setQuantity(existing.getQuantity() + quantity);
                    return cartRepo.save(existing);
                })
                .orElseGet(() -> cartRepo.save(CartItem.builder()
                        .user(user)
                        .product(product)
                        .quantity(quantity)
                        .build()));

        return new CartItemDTO(
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getProduct().getDescription(),
                item.getProduct().getImage(),
                item.getProduct().getPrice(),
                item.getQuantity(),
                item.getProduct().getQuantity(),
                item.getProduct().getPrice() * item.getQuantity()
        );
    }

    @Transactional
    public void removeFromCart(String email, Long productId) {
        // suppression transactionnelle pour éviter les effets de concurrence
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
        cartRepo.deleteByUserAndProductId(user, productId);
    }

    public void clearCart(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
        cartRepo.deleteAll(cartRepo.findByUser(user)); // suppression complète du panier
    }

    public CartItemDTO updateQuantity(String email, Long productId, int quantity) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

        CartItem item = cartRepo.findByUserAndProductId(user, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found in cart: id=" + productId));

        item.setQuantity(quantity);
        cartRepo.save(item);

        Product product = item.getProduct();

        // recalcul du total après maj
        return new CartItemDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getImage(),
                product.getPrice(),
                item.getQuantity(),
                product.getQuantity(),
                product.getPrice() * item.getQuantity()
        );
    }
}
