package com.application.altenshop.dtos;

import com.application.altenshop.models.InventoryStatus;
import jakarta.validation.constraints.*;

public record ProductCreateDTO(
        @NotBlank String code,
        @NotBlank String name,
        @Size(max = 2000) String description,
        String image,
        String category,
        @NotNull @PositiveOrZero Double price,
        @NotNull @PositiveOrZero Integer quantity,
        String internalReference,
        Long shellId,
        @NotNull InventoryStatus inventoryStatus,
        @NotNull @Min(0) @Max(5) Integer rating
) {}
