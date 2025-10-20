package com.application.altenshop.dtos;

public record CartItemDTO(
        Long productId,
        String productName,
        String productDescription,
        String productImage,
        Double price,
        Integer quantity,
        Integer availableQuantity,
        Double total
) {}
