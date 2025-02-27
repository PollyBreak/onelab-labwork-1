package com.polina.lab1.repository;

import com.polina.lab1.dto.ProductDTO;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ProductRepositroyImpl implements ProductRepository{
    private final List<ProductDTO> productRepository = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(1l);


    @Override
    public void save(ProductDTO product) {
        if (product.getId() == null) {
            product.setId(idCounter.getAndIncrement());
            productRepository.add(product);
        } else {
            delete(product.getId());
            productRepository.add(product);
        }
    }

    @Override
    public void delete(Long id) {
        productRepository.removeIf(product->product.getId().equals(id));
    }

    @Override
    public ProductDTO findById(Long productId) {
        return productRepository
                .stream()
                .filter(product-> product.getId()!=null && product.getId().equals(productId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<ProductDTO> findAll() {
        return new ArrayList<>(productRepository);
    }

    @Override
    public ProductDTO findByName(String productName) {
        return productRepository
                .stream()
                .filter(product-> product.getId()!=null && product.getName().equals(productName))
                .findFirst()
                .orElse(null);
    }
}
