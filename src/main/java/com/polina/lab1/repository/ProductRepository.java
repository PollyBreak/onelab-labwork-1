package com.polina.lab1.repository;

import com.polina.lab1.dto.ProductDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductDTO, Long> {
    Optional<ProductDTO> findByName(String productName);
}
