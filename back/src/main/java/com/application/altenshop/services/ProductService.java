package com.application.altenshop.services;

import com.application.altenshop.dtos.*;
import com.application.altenshop.models.InventoryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    Page<ProductResponseDTO> list(String q, String category, InventoryStatus status, Pageable pageable);

    ProductResponseDTO get(Long id);

    ProductResponseDTO create(ProductCreateDTO dto);

    ProductResponseDTO update(Long id, ProductUpdateDTO dto);

    void delete(Long id);
}
