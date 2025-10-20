package com.application.altenshop.services;

import com.application.altenshop.dtos.WishlistItemDTO;
import java.util.List;

public interface WishlistService {

    List<WishlistItemDTO> getWishlist(String email);

    WishlistItemDTO addToWishlist(String email, Long productId);

    void removeFromWishlist(String email, Long productId);
}
