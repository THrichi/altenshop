package com.application.altenshop.controllers;

import com.application.altenshop.dtos.*;
import com.application.altenshop.models.InventoryStatus;
import com.application.altenshop.security.AdminGuard;
import com.application.altenshop.services.impl.ProductServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductServiceImpl service;
    private final AdminGuard adminGuard;

    // Liste paginée + filtres (search, catégorie, statut, tri)
    @GetMapping
    public Page<ProductResponseDTO> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) InventoryStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "createdAt,DESC") String sort) {

        // parsing manuel du paramètre sort (ex: name,ASC)
        String[] s = sort.split(",");
        Sort.Direction dir = (s.length > 1 && s[1].equalsIgnoreCase("ASC")) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, s[0]));

        return service.list(q, category, status, pageable);
    }

    @GetMapping("/{id}")
    public ProductResponseDTO get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> create(@RequestBody @Valid ProductCreateDTO dto,
                                                     Authentication auth) {
        // vérifie que l’utilisateur est admin avant création
        adminGuard.ensureAdmin(auth);
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> update(@PathVariable Long id,
                                                     @RequestBody @Valid ProductUpdateDTO dto,
                                                     Authentication auth) {
        // même contrôle d’accès côté modification
        adminGuard.ensureAdmin(auth);
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        adminGuard.ensureAdmin(auth);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
