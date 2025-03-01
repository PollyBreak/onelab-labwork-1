package com.polina.lab1.repository;

import com.polina.lab1.dto.ProductDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class ProductRepositoryImpl implements ProductRepository{
    private final JdbcTemplate jdbcTemplate;

    public ProductRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<ProductDTO> productMapper = (rs, rowNum) ->
            ProductDTO.builder()
                    .id(rs.getLong("id"))
                    .name(rs.getString("name"))
                    .description(rs.getString("description"))
                    .build();

    public void save(ProductDTO product) {
        jdbcTemplate.update("INSERT INTO products (name, description) VALUES (?, ?)",
                product.getName(), product.getDescription());
    }

    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM products WHERE id = ?", id);
    }

    public ProductDTO findById(Long productId) {
        List<ProductDTO> products = jdbcTemplate.query("SELECT * FROM products WHERE id = ?", productMapper, productId);
        return products.isEmpty() ? null : products.get(0);
    }

    public List<ProductDTO> findAll() {
        return jdbcTemplate.query("SELECT * FROM products", productMapper);
    }

    public ProductDTO findByName(String productName) {
        List<ProductDTO> products = jdbcTemplate.query("SELECT * FROM products WHERE name = ?", productMapper, productName);
        return products.isEmpty() ? null : products.get(0);
    }
}
