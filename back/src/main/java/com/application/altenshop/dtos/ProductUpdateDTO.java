package com.application.altenshop.dtos;

import com.application.altenshop.models.InventoryStatus;

public record ProductUpdateDTO(
        String name,
        String code,
        String description,
        String image,
        String category,
        Double price,
        Integer quantity,
        String internalReference,
        Long shellId,
        InventoryStatus inventoryStatus,
        Integer rating
) {}
