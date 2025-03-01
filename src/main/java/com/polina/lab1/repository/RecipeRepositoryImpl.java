package com.polina.lab1.repository;

import com.polina.lab1.dto.RecipeDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RecipeRepositoryImpl implements RecipeRepository{
    private final JdbcTemplate jdbcTemplate;

    public RecipeRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<RecipeDTO> recipeMapper = (rs, rowNum) ->
            RecipeDTO.builder()
                    .id(rs.getLong("id"))
                    .title(rs.getString("title"))
                    .description(rs.getString("description"))
                    .instructions(rs.getString("instructions"))
                    .authorId(rs.getLong("author_id"))
                    .build();

    @Override
    public void save(RecipeDTO recipe) {
        jdbcTemplate.update("INSERT INTO recipes (title, description, instructions, author_id) VALUES (?, ?, ?, ?)",
                recipe.getTitle(), recipe.getDescription(), recipe.getInstructions(), recipe.getAuthorId());

        Long recipeId = jdbcTemplate.queryForObject("SELECT MAX(id) FROM recipes", Long.class);
        recipe.setId(recipeId);

        for (Long productId : recipe.getProductIds()) {
            jdbcTemplate.update("INSERT INTO recipe_products (recipe_id, product_id) VALUES (?, ?)", recipeId, productId);
        }
    }


    public void delete(Long recipeId) {
        jdbcTemplate.update("DELETE FROM recipes WHERE id = ?", recipeId);
    }

    public RecipeDTO findById(Long recipeId) {
        List<RecipeDTO> recipes = jdbcTemplate.query("SELECT * FROM recipes WHERE id = ?", recipeMapper, recipeId);
        return recipes.isEmpty() ? null : recipes.get(0);
    }

    public List<RecipeDTO> findAll() {
        return jdbcTemplate.query("SELECT * FROM recipes", recipeMapper);
    }

    public List<RecipeDTO> findByProducts(List<Long> productIds) {
        String placeholders = String.join(",", productIds.stream().map(id -> "?").toList());
        String sql = "SELECT r.* FROM recipes r JOIN recipe_products rp ON r.id = rp.recipe_id WHERE rp.product_id IN (" + placeholders + ")";

        return jdbcTemplate.query(sql, recipeMapper, productIds.toArray());
    }

    public List<RecipeDTO> findByAuthorId(Long authorId) {
        return jdbcTemplate.query("SELECT * FROM recipes WHERE author_id = ?", recipeMapper, authorId);
    }

}
