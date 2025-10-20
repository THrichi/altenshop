package com.application.altenshop.services.impl;

import com.application.altenshop.dtos.WishlistItemDTO;
import com.application.altenshop.exceptions.ResourceNotFoundException;
import com.application.altenshop.models.*;
import com.application.altenshop.repositories.*;
import com.application.altenshop.services.WishlistService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepo;
    private final ProductRepository productRepo;
    private final UserRepository userRepo;

    public List<WishlistItemDTO> getWishlist(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

        // map entités → DTO pour exposer uniquement les infos produit utiles
        return wishlistRepo.findByUser(user)
                .stream()
                .map(item -> new WishlistItemDTO(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getProduct().getImage(),
                        item.getProduct().getCategory(),
                        item.getProduct().getPrice()
                ))
                .toList();
    }

    public WishlistItemDTO addToWishlist(String email, Long productId) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: id=" + productId));

        // évite les doublons : si déjà présent → on ne recrée pas l’entrée
        WishlistItem item = wishlistRepo.findByUserAndProductId(user, productId)
                .orElseGet(() -> wishlistRepo.save(WishlistItem.builder()
                        .user(user)
                        .product(product)
                        .build()));

        return new WishlistItemDTO(
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getProduct().getImage(),
                item.getProduct().getCategory(),
                item.getProduct().getPrice()
        );
    }

    @Transactional
    public void removeFromWishlist(String email, Long productId) {
        // suppression transactionnelle : garantit cohérence même en cas d’accès concurrent
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
        wishlistRepo.deleteByUserAndProductId(user, productId);
    }
}
