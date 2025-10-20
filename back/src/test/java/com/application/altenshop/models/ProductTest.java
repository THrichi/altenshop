package com.application.altenshop.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    void testBuilderAndGetters() {
        Product product = Product.builder()
                .id(1L)
                .code("CPU-001")
                .name("Ryzen 9")
                .description("Powerful CPU")
                .image("image.jpg")
                .category("Component")
                .price(499.99)
                .quantity(20)
                .internalReference("INT-123")
                .shellId(2L)
                .inventoryStatus(InventoryStatus.INSTOCK)
                .rating(5)
                .createdAt(100L)
                .updatedAt(200L)
                .build();

        assertAll(
                () -> assertEquals(1L, product.getId()),
                () -> assertEquals("CPU-001", product.getCode()),
                () -> assertEquals("Ryzen 9", product.getName()),
                () -> assertEquals("Powerful CPU", product.getDescription()),
                () -> assertEquals("image.jpg", product.getImage()),
                () -> assertEquals("Component", product.getCategory()),
                () -> assertEquals(499.99, product.getPrice()),
                () -> assertEquals(20, product.getQuantity()),
                () -> assertEquals("INT-123", product.getInternalReference()),
                () -> assertEquals(2L, product.getShellId()),
                () -> assertEquals(InventoryStatus.INSTOCK, product.getInventoryStatus()),
                () -> assertEquals(5, product.getRating()),
                () -> assertEquals(100L, product.getCreatedAt()),
                () -> assertEquals(200L, product.getUpdatedAt())
        );
    }

    @Test
    void testEqualsAndHashCodeSameValues() {
        Product p1 = new Product(1L, "CODE", "Laptop", "desc", "img", "Component",
                1000.0, 10, "INT", 1L, InventoryStatus.INSTOCK, 5, 0L, 0L);
        Product p2 = new Product(1L, "CODE", "Laptop", "desc", "img", "Component",
                1000.0, 10, "INT", 1L, InventoryStatus.INSTOCK, 5, 0L, 0L);

        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
        assertTrue(p1.toString().contains("Laptop"));
    }

    @Test
    void testEqualsAndHashCodeDifferentObjects() {
        Product p1 = Product.builder().id(1L).code("C1").name("A").build();
        Product p2 = Product.builder().id(2L).code("C2").name("B").build();

        assertNotEquals(p1, p2);
        assertNotEquals(p1.hashCode(), p2.hashCode());
        assertNotEquals(p1, null);
        assertNotEquals(p1, "someString");
    }

    @Test
    void testSetterMethods() {
        Product product = new Product();
        product.setId(3L);
        product.setCode("GPU-001");
        product.setName("RTX 4080");
        product.setDescription("High-end GPU");
        product.setImage("gpu.jpg");
        product.setCategory("Component");
        product.setPrice(1299.99);
        product.setQuantity(5);
        product.setInternalReference("INT-GPU");
        product.setShellId(4L);
        product.setInventoryStatus(InventoryStatus.OUTOFSTOCK);
        product.setRating(4);
        product.setCreatedAt(10L);
        product.setUpdatedAt(20L);

        assertEquals("RTX 4080", product.getName());
        assertEquals(InventoryStatus.OUTOFSTOCK, product.getInventoryStatus());
    }

    @Test
    void testInventoryStatusEnumCoverage() {
        for (InventoryStatus status : InventoryStatus.values()) {
            assertNotNull(status.toString());
        }
    }
}
