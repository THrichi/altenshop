package com.application.altenshop.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WishlistItemTest {

    @Test
    void testWishlistBuilder() {
        User user = new User(1L, "john", "John", "john@example.com", "pwd");
        Product product = Product.builder()
                .id(1L)
                .name("Headphones")
                .price(199.99)
                .build();

        WishlistItem item = WishlistItem.builder()
                .id(1L)
                .user(user)
                .product(product)
                .build();

        assertEquals("Headphones", item.getProduct().getName());
        assertEquals(user, item.getUser());
    }

    @Test
    void testWishlistEquality() {
        WishlistItem w1 = WishlistItem.builder().id(1L).build();
        WishlistItem w2 = WishlistItem.builder().id(1L).build();

        assertEquals(w1, w2);
        assertEquals(w1.hashCode(), w2.hashCode());
    }
}