package com.application.altenshop.services.impl;

import com.application.altenshop.dtos.*;
import com.application.altenshop.models.*;
import com.application.altenshop.repositories.ProductRepository;
import com.application.altenshop.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repo;

    public Page<ProductResponseDTO> list(String q, String category, InventoryStatus status, Pageable pageable) {
        Page<Product> page;

        // priorise les filtres dans l’ordre : recherche → catégorie → statut → tout
        if (q != null && !q.isBlank()) {
            page = repo.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(q, q, pageable);
        } else if (category != null && !category.isBlank()) {
            page = repo.findByCategoryIgnoreCase(category, pageable);
        } else if (status != null) {
            page = repo.findByInventoryStatus(status, pageable);
        } else {
            page = repo.findAll(pageable);
        }

        // map direct vers DTO pour ne jamais exposer les entités
        return page.map(this::toDto);
    }

    public ProductResponseDTO get(Long id) {
        return toDto(repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found")));
    }

    public ProductResponseDTO create(ProductCreateDTO dto) {
        // pas de validation métier ici → supposé géré côté contrôleur ou DTO
        Product p = Product.builder()
                .code(dto.code())
                .name(dto.name())
                .description(dto.description())
                .image(dto.image())
                .category(dto.category())
                .price(dto.price())
                .quantity(dto.quantity())
                .internalReference(dto.internalReference())
                .shellId(dto.shellId())
                .inventoryStatus(dto.inventoryStatus())
                .rating(dto.rating())
                .build();

        return toDto(repo.save(p));
    }

    public ProductResponseDTO update(Long id, ProductUpdateDTO dto) {
        Product p = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // mise à jour champ par champ si non nul (patch-like)
        if (dto.name() != null) p.setName(dto.name());
        if (dto.code() != null) p.setCode(dto.code());
        if (dto.description() != null) p.setDescription(dto.description());
        if (dto.image() != null) p.setImage(dto.image());
        if (dto.category() != null) p.setCategory(dto.category());
        if (dto.price() != null) p.setPrice(dto.price());
        if (dto.quantity() != null) p.setQuantity(dto.quantity());
        if (dto.internalReference() != null) p.setInternalReference(dto.internalReference());
        if (dto.shellId() != null) p.setShellId(dto.shellId());
        if (dto.inventoryStatus() != null) p.setInventoryStatus(dto.inventoryStatus());
        if (dto.rating() != null) p.setRating(dto.rating());

        return toDto(repo.save(p));
    }

    public void delete(Long id) {
        // simple check avant suppression pour éviter un delete silencieux
        if (!repo.existsById(id)) throw new RuntimeException("Product not found");
        repo.deleteById(id);
    }

    private ProductResponseDTO toDto(Product p) {
        // conversion centralisée vers DTO → évite duplication dans les méthodes CRUD
        return ProductResponseDTO.builder()
                .id(p.getId())
                .code(p.getCode())
                .name(p.getName())
                .description(p.getDescription())
                .image(p.getImage())
                .category(p.getCategory())
                .price(p.getPrice())
                .quantity(p.getQuantity())
                .internalReference(p.getInternalReference())
                .shellId(p.getShellId())
                .inventoryStatus(p.getInventoryStatus())
                .rating(p.getRating())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}
