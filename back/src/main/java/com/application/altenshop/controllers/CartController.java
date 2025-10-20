package com.application.altenshop.controllers;

import com.application.altenshop.dtos.CartItemDTO;
import com.application.altenshop.services.impl.CartServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartServiceImpl service;

    @GetMapping
    public ResponseEntity<List<CartItemDTO>> getCart(Authentication auth) {
        // l'email (ou username) est extrait depuis le token JWT via Authentication
        return ResponseEntity.ok(service.getCart(auth.getName()));
    }

    @PostMapping("/{productId}")
    public ResponseEntity<CartItemDTO> addToCart(@PathVariable Long productId,
                                                 @RequestParam(defaultValue = "1") int quantity,
                                                 Authentication auth) {
        // @RequestParam utilisé pour gérer facilement la quantité via l'URL (ex: ?quantity=2)
        return ResponseEntity.ok(service.addToCart(auth.getName(), productId, quantity));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<CartItemDTO> updateQuantity(@PathVariable Long productId,
                                                      @RequestParam int quantity,
                                                      Authentication auth) {
        // update uniquement la quantité d’un article déjà présent
        return ResponseEntity.ok(service.updateQuantity(auth.getName(), productId, quantity));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> remove(@PathVariable Long productId, Authentication auth) {
        service.removeFromCart(auth.getName(), productId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clear(Authentication auth) {
        // supprime tout le panier de l’utilisateur courant
        service.clearCart(auth.getName());
        return ResponseEntity.noContent().build();
    }
}

