package com.polina.lab1.repository;

import com.polina.lab1.dto.RecipeDTO;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class RecipeRepositoryImpl implements RecipeRepository{
    private final ArrayList<RecipeDTO> recipeRepository = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    @Override
    public void save(RecipeDTO recipeDTO) {
        if (recipeDTO.getId() == null){
            recipeDTO.setId(idCounter.getAndIncrement());
            recipeRepository.add(recipeDTO);
        } else {
            delete(recipeDTO.getId());
            recipeRepository.add(recipeDTO);
        }
    }

    @Override
    public void delete(Long recipeId) {
        recipeRepository.removeIf(recipe -> recipe.getId().equals(recipeId));
    }

    @Override
    public RecipeDTO findById(Long recipeId) {
        return recipeRepository
                .stream()
                .filter(recipe-> recipe.getId() != null && recipe.getId().equals(recipeId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<RecipeDTO> findAll() {
        return new ArrayList<>(recipeRepository);
    }

    @Override
    public List<RecipeDTO> findByProducts(List<Long> productIds) {
        return recipeRepository
                .stream()
                .filter(recipe-> recipe.getId() != null && recipe.getProductIds().containsAll(productIds))
                .toList();
    }
}
