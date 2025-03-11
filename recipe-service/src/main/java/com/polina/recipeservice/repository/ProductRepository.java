package com.polina.recipeservice.repository;

import com.polina.recipeservice.dto.ProductDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<ProductDTO, Long> {
    ProductDTO findByName(String name);
}
