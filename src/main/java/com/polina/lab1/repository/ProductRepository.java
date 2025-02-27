package com.polina.lab1.repository;

import com.polina.lab1.dto.ProductDTO;

import java.util.List;

public interface ProductRepository {
    void save(ProductDTO product);
    void delete(Long id);
    ProductDTO findById(Long productId);
    List<ProductDTO> findAll();
    ProductDTO findByName(String productName);
}
