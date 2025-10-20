package com.application.altenshop.dtos;

import com.application.altenshop.models.InventoryStatus;
import lombok.Builder;

@Builder
public record ProductResponseDTO(
        Long id,
        String code,
        String name,
        String description,
        String image,
        String category,
        Double price,
        Integer quantity,
        String internalReference,
        Long shellId,
        InventoryStatus inventoryStatus,
        Integer rating,
        Long createdAt,
        Long updatedAt
) {}
