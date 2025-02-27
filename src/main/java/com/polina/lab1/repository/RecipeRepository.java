package com.polina.lab1.repository;

import com.polina.lab1.dto.RecipeDTO;

import java.util.List;

public interface RecipeRepository {
    void save (RecipeDTO recipeDTO);
    void delete(Long recipeId);
    RecipeDTO findById(Long recipeId);
    List<RecipeDTO> findAll();
    List<RecipeDTO> findByProducts(List<Long> productIds);
}
