package com.application.altenshop.services;


import com.application.altenshop.dtos.*;
import com.application.altenshop.models.*;
import com.application.altenshop.repositories.ProductRepository;
import com.application.altenshop.services.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {

    private ProductRepository repo;
    private ProductServiceImpl service;

    private Product product;

    @BeforeEach
    void setUp() {
        repo = mock(ProductRepository.class);
        service = new ProductServiceImpl(repo);

        product = Product.builder()
                .id(1L)
                .code("CPU-001")
                .name("AMD Ryzen 9 7950X")
                .description("High-end CPU")
                .image("url")
                .category("Component")
                .price(699.99)
                .quantity(10)
                .internalReference("INT-001")
                .shellId(5L)
                .inventoryStatus(InventoryStatus.INSTOCK)
                .rating(5)
                .build();
    }

    // =========================
    // list()
    // =========================
    @Test
    void list_ShouldSearchByQuery_WhenQProvided() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(List.of(product));

        when(repo.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase("cpu", "cpu", pageable))
                .thenReturn(page);

        Page<ProductResponseDTO> result = service.list("cpu", null, null, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("AMD Ryzen 9 7950X", result.getContent().get(0).name());
    }

    @Test
    void list_ShouldFilterByCategory_WhenCategoryProvided() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(List.of(product));

        when(repo.findByCategoryIgnoreCase("Component", pageable)).thenReturn(page);

        Page<ProductResponseDTO> result = service.list(null, "Component", null, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Component", result.getContent().get(0).category());
    }

    @Test
    void list_ShouldFilterByStatus_WhenStatusProvided() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(List.of(product));

        when(repo.findByInventoryStatus(InventoryStatus.INSTOCK, pageable)).thenReturn(page);

        Page<ProductResponseDTO> result = service.list(null, null, InventoryStatus.INSTOCK, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(InventoryStatus.INSTOCK, result.getContent().get(0).inventoryStatus());
    }

    @Test
    void list_ShouldReturnAll_WhenNoFilter() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(List.of(product));

        when(repo.findAll(pageable)).thenReturn(page);

        Page<ProductResponseDTO> result = service.list(null, null, null, pageable);

        assertEquals(1, result.getTotalElements());
    }

    // =========================
    // get()
    // =========================
    @Test
    void get_ShouldReturnProduct_WhenFound() {
        when(repo.findById(1L)).thenReturn(Optional.of(product));

        ProductResponseDTO dto = service.get(1L);

        assertEquals("AMD Ryzen 9 7950X", dto.name());
        assertEquals(699.99, dto.price());
    }

    @Test
    void get_ShouldThrow_WhenNotFound() {
        when(repo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.get(1L));
    }

    // =========================
    // create()
    // =========================
    @Test
    void create_ShouldSaveProduct() {
        ProductCreateDTO dto = new ProductCreateDTO(
                "CPU-001", "AMD Ryzen 9", "desc", "img", "Component",
                500.0, 20, "INT", 2L, InventoryStatus.INSTOCK, 5
        );

        when(repo.save(any(Product.class))).thenReturn(product);

        ProductResponseDTO result = service.create(dto);

        assertEquals("AMD Ryzen 9 7950X", result.name());
        verify(repo).save(any(Product.class));
    }

    // =========================
    // update()
    // =========================
    @Test
    void update_ShouldModifyFields_WhenPresent() {
        ProductUpdateDTO dto = new ProductUpdateDTO(
                "New Name", "NEWCODE", "Updated", "new.png", "NewCat",
                1000.0, 5, "INTNEW", 7L, InventoryStatus.LOWSTOCK, 4
        );

        when(repo.findById(1L)).thenReturn(Optional.of(product));
        when(repo.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        ProductResponseDTO result = service.update(1L, dto);

        assertEquals("New Name", result.name());
        assertEquals("NEWCODE", result.code());
        assertEquals(1000.0, result.price());
        verify(repo).save(any(Product.class));
    }

    @Test
    void update_ShouldThrow_WhenProductNotFound() {
        when(repo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.update(1L, new ProductUpdateDTO(null, null, null, null, null, null, null, null, null, null, null)));
    }

    // =========================
    // 🔹 delete()
    // =========================
    @Test
    void delete_ShouldRemove_WhenExists() {
        when(repo.existsById(1L)).thenReturn(true);
        doNothing().when(repo).deleteById(1L);

        service.delete(1L);

        verify(repo).deleteById(1L);
    }

    @Test
    void delete_ShouldThrow_WhenNotFound() {
        when(repo.existsById(1L)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> service.delete(1L));
    }
}
