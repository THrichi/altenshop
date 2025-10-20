package com.application.altenshop.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CartItemTest {

    @Test
    void testCartItemBuilder() {
        User user = new User(1L, "john", "John", "john@example.com", "pwd");
        Product product = Product.builder()
                .id(1L)
                .name("Laptop")
                .price(1000.0)
                .build();

        CartItem item = CartItem.builder()
                .id(1L)
                .user(user)
                .product(product)
                .quantity(2)
                .build();

        assertEquals(2, item.getQuantity());
        assertEquals("Laptop", item.getProduct().getName());
        assertEquals(user, item.getUser());
    }

    @Test
    void testCartItemEquality() {
        CartItem i1 = CartItem.builder().id(1L).quantity(1).build();
        CartItem i2 = CartItem.builder().id(1L).quantity(1).build();

        assertEquals(i1, i2);
        assertEquals(i1.hashCode(), i2.hashCode());
    }
}
