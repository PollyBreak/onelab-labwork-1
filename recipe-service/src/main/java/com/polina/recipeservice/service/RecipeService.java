package com.polina.recipeservice.service;

import com.polina.recipeservice.dto.ProductDTO;
import com.polina.recipeservice.dto.RecipeDTO;
import com.polina.recipeservice.dto.ReviewDTO;
import com.polina.recipeservice.dto.UserPreferencesDTO;
import com.polina.recipeservice.repository.ProductRepository;
import com.polina.recipeservice.repository.RecipeRepository;
import com.polina.recipeservice.repository.ReviewRepository;
import com.polina.recipeservice.repository.UserPreferencesRepository;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecipeService {
    @Autowired
    public RecipeRepository recipeRepository;
    @Autowired
    public ProductRepository productRepository;
    @Autowired
    public ReviewRepository reviewRepository;

    @Autowired
    public UserPreferencesRepository preferencesRepository;

    @Transactional
    public void addReview(Long recipeId, Long userId, int rating, String comment) {
        RecipeDTO recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found with ID: " + recipeId));

        recipe.getProducts().size();

        ReviewDTO review = ReviewDTO.builder()
                .recipeId(recipeId)
                .userId(userId)
                .rating(rating)
                .comment(comment)
                .build();

        reviewRepository.save(review);

        double updatedRating = getAverageRating(recipeId);
        System.out.println("Updated Rating for Recipe ID " + recipeId + ": " + updatedRating);
    }

    @Transactional
    public double getAverageRating(Long recipeId) {
        List<ReviewDTO> reviews = reviewRepository.findByRecipeId(recipeId);
        if (reviews.isEmpty()) {
            return 0.0;
        }
        return reviews.stream().mapToInt(ReviewDTO::getRating).average().orElse(0.0);
    }


    public void saveUserPreferences(Long userId, List<String> ingredients) {
        UserPreferencesDTO preferences = new UserPreferencesDTO(userId, ingredients);
        preferencesRepository.save(preferences);
    }


    @Transactional
    public List<RecipeDTO> getRecommendedRecipes(Long userId) {
        Optional<UserPreferencesDTO> preferences = preferencesRepository.findById(userId);
        if (preferences.isEmpty()) {
            return List.of();
        }

        List<String> favoriteIngredients = preferences.get().getFavoriteIngredients()
                .stream()
                .map(String::trim)
                .collect(Collectors.toList());

        List<RecipeDTO> recommendedRecipes = recipeRepository.getAllRecipes().stream()
                .filter(recipe -> recipe.getProducts().stream()
                        .map(product -> product.getName().trim())
                        .anyMatch(favoriteIngredients::contains))
                .collect(Collectors.toList());

        recommendedRecipes.forEach(recipe -> Hibernate.initialize(recipe.getProducts()));

        recommendedRecipes.forEach(recipe -> recipe.setAverageRating(getAverageRating(recipe.getId())));

        recommendedRecipes.sort(Comparator.comparingDouble(RecipeDTO::getAverageRating).reversed());

        return recommendedRecipes;
    }




    @Transactional
    public List<RecipeDTO> getAllRecipes() {
        List<RecipeDTO> recipes = recipeRepository.getAllRecipes();
        recipes.forEach(recipe -> {
            Hibernate.initialize(recipe.getProducts());
            recipe.setAverageRating(getAverageRating(recipe.getId()));
        });

        return recipes;
    }

    @Transactional
    public List<RecipeDTO> getRecipesByUser(Long userId) {
        List<RecipeDTO> recipes = recipeRepository.findByAuthorId(userId);
        recipes.forEach(recipe -> recipe.getProducts().size());
        return recipes;
    }

    public void addRecipe(Long userId, RecipeDTO recipe, List<ProductDTO> products) {
        List<ProductDTO> savedProducts = new ArrayList<>();
        for (ProductDTO product : products) {
            ProductDTO existingProduct = productRepository.findByName(product.getName());
            if (existingProduct == null) {
                existingProduct = productRepository.save(product);
            }
            savedProducts.add(existingProduct);
        }

        recipe.setAuthorId(userId);
        recipe.setProducts(savedProducts);
        recipeRepository.save(recipe);
        System.out.println("Recipe saved successfully: " + recipe.getTitle());
    }



}

