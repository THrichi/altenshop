package com.application.altenshop.controllers;

import com.application.altenshop.dtos.WishlistItemDTO;
import com.application.altenshop.services.WishlistService;
import com.application.altenshop.services.impl.WishlistServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService service;

    @GetMapping
    public ResponseEntity<List<WishlistItemDTO>> getWishlist(Authentication auth) {
        // récupère la wishlist liée à l'utilisateur courant (identifié via le JWT)
        return ResponseEntity.ok(service.getWishlist(auth.getName()));
    }

    @PostMapping("/{productId}")
    public ResponseEntity<WishlistItemDTO> add(@PathVariable Long productId, Authentication auth) {
        // ajoute un produit à la wishlist de l'utilisateur connecté
        return ResponseEntity.ok(service.addToWishlist(auth.getName(), productId));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> remove(@PathVariable Long productId, Authentication auth) {
        service.removeFromWishlist(auth.getName(), productId);
        return ResponseEntity.noContent().build();
    }
}
