    package com.application.altenshop.dtos;

    public record WishlistItemDTO(
            Long productId,
            String productName,
            String productImage,
            String category,
            Double price
    ) {}
