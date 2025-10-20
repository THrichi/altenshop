package com.application.altenshop.controllers;

import com.application.altenshop.dtos.*;
import com.application.altenshop.models.InventoryStatus;
import com.application.altenshop.security.AdminGuard;
import com.application.altenshop.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductControllerTest {

    private ProductController controller;

    @Mock
    private ProductService productService;

    @Mock
    private AdminGuard adminGuard;

    @Mock
    private Authentication auth;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new ProductController(productService, adminGuard);
        when(auth.getName()).thenReturn("admin@example.com");
    }




    @Test
    void testGetProductById() {
        ProductResponseDTO dto = ProductResponseDTO.builder()
                .id(1L)
                .code("CPU-001")
                .name("Ryzen 9")
                .description("High-end CPU")
                .image("img.png")
                .category("Component")
                .price(499.99)
                .quantity(20)
                .inventoryStatus(InventoryStatus.INSTOCK)
                .rating(5)
                .createdAt(123L)
                .updatedAt(456L)
                .build();

        when(productService.get(1L)).thenReturn(dto);

        ProductResponseDTO result = controller.get(1L);

        assertEquals("Ryzen 9", result.name());
        assertEquals(499.99, result.price());
        verify(productService).get(1L);
    }

    @Test
    void testCreateProduct() {
        ProductCreateDTO createDTO = new ProductCreateDTO(
                "CPU-001",
                "Ryzen 9",
                "High-end CPU",
                "img.png",
                "Component",
                499.99,
                20,
                "INT-001",
                1L,
                InventoryStatus.INSTOCK,
                5
        );

        ProductResponseDTO responseDTO = ProductResponseDTO.builder()
                .id(1L)
                .code("CPU-001")
                .name("Ryzen 9")
                .description("High-end CPU")
                .image("img.png")
                .category("Component")
                .price(499.99)
                .quantity(20)
                .inventoryStatus(InventoryStatus.INSTOCK)
                .rating(5)
                .createdAt(100L)
                .updatedAt(200L)
                .build();

        doNothing().when(adminGuard).ensureAdmin(auth);
        when(productService.create(createDTO)).thenReturn(responseDTO);

        ResponseEntity<ProductResponseDTO> response = controller.create(createDTO, auth);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Ryzen 9", response.getBody().name());
        verify(adminGuard).ensureAdmin(auth);
        verify(productService).create(createDTO);
    }

    @Test
    void testUpdateProduct() {
        ProductUpdateDTO updateDTO = new ProductUpdateDTO(
                "Ryzen 9X",
                "CPU-002",
                "Upgraded CPU",
                "img2.png",
                "Component",
                599.99,
                10,
                "INT-002",
                2L,
                InventoryStatus.LOWSTOCK,
                4
        );

        ProductResponseDTO updated = ProductResponseDTO.builder()
                .id(1L)
                .code("CPU-002")
                .name("Ryzen 9X")
                .description("Upgraded CPU")
                .image("img2.png")
                .category("Component")
                .price(599.99)
                .quantity(10)
                .inventoryStatus(InventoryStatus.LOWSTOCK)
                .rating(4)
                .createdAt(100L)
                .updatedAt(200L)
                .build();

        doNothing().when(adminGuard).ensureAdmin(auth);
        when(productService.update(1L, updateDTO)).thenReturn(updated);

        ResponseEntity<ProductResponseDTO> response = controller.update(1L, updateDTO, auth);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Ryzen 9X", response.getBody().name());
        verify(adminGuard).ensureAdmin(auth);
        verify(productService).update(1L, updateDTO);
    }

    @Test
    void testDeleteProduct() {
        doNothing().when(adminGuard).ensureAdmin(auth);
        doNothing().when(productService).delete(1L);

        ResponseEntity<Void> response = controller.delete(1L, auth);

        assertEquals(204, response.getStatusCodeValue());
        verify(adminGuard).ensureAdmin(auth);
        verify(productService).delete(1L);
    }
}
