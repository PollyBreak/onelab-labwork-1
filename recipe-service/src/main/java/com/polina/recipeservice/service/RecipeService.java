package com.polina.recipeservice.service;

import com.polina.recipeservice.client.UserClient;
import com.polina.recipeservice.dto.RecipeDTO;
import com.polina.recipeservice.dto.ReviewDTO;
import com.polina.recipeservice.entity.Product;
import com.polina.recipeservice.entity.Recipe;
import com.polina.recipeservice.entity.Review;
import com.polina.recipeservice.repository.ProductRepository;
import com.polina.recipeservice.repository.RecipeRepository;
import com.polina.recipeservice.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;
    private final UserClient userClient;

    public RecipeService(RecipeRepository recipeRepository, ProductRepository productRepository,
                         ReviewRepository reviewRepository, UserClient userClient) {
        this.recipeRepository = recipeRepository;
        this.productRepository = productRepository;
        this.reviewRepository = reviewRepository;
        this.userClient = userClient;
    }

    // ✅ Create Recipe
    public void createRecipe(RecipeDTO recipeDTO) {
        // Ensure user exists
        try {
            userClient.checkUserExists(recipeDTO.getAuthorId());
        } catch (Exception e) {
            throw new IllegalArgumentException("User with ID " + recipeDTO.getAuthorId() + " does not exist.");
        }

        List<Product> products = recipeDTO.getProducts().stream()
                .map(name -> productRepository.findByName(name) != null ? productRepository.findByName(name) : productRepository.save(new Product(null, name)))
                .collect(Collectors.toList());

        Recipe recipe = Recipe.builder()
                .title(recipeDTO.getTitle())
                .description(recipeDTO.getDescription())
                .instructions(recipeDTO.getInstructions())
                .authorId(recipeDTO.getAuthorId())
                .products(products)
                .build();

        recipeRepository.save(recipe);
    }

    // ✅ Get All Recipes
    public List<RecipeDTO> getAllRecipes() {
        return recipeRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ✅ Get Recipes by User ID
    public List<RecipeDTO> getRecipesByUser(Long userId) {
        return recipeRepository.findByAuthorId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ✅ Get Recommended Recipes for a User
    public List<RecipeDTO> getRecommendedRecipes(Long userId) {
        try {
            ResponseEntity<Map<String, Object>> response = userClient.getUserPreferences(userId);
            Map<String, Object> responseBody = response.getBody();

            if (responseBody == null || !responseBody.containsKey("favoriteIngredients")) {
                return Collections.emptyList();
            }

            List<String> favoriteIngredients = (List<String>) responseBody.get("favoriteIngredients");

            return recipeRepository.findAll().stream()
                    .filter(recipe -> recipe.getProducts().stream()
                            .map(Product::getName)
                            .anyMatch(favoriteIngredients::contains))
                    .map(this::convertToDTO)
                    .sorted(Comparator.comparingDouble(RecipeDTO::getAverageRating).reversed())  // Sort by rating
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public void addReview(Long recipeId, ReviewDTO reviewDTO) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found with ID: " + recipeId));

        // ✅ Check if the user has already reviewed this specific recipe
        Optional<Review> existingReview = reviewRepository.findByRecipeIdAndUserId(recipeId, reviewDTO.getUserId());

        if (existingReview.isPresent()) {
            // ✅ Update existing review if found
            Review reviewToUpdate = existingReview.get();
            reviewToUpdate.setRating(reviewDTO.getRating());
            reviewToUpdate.setComment(reviewDTO.getComment());
            reviewRepository.save(reviewToUpdate);
        } else {
            // ✅ If no existing review, create a new one
            Review newReview = Review.builder()
                    .recipeId(recipeId)
                    .userId(reviewDTO.getUserId())
                    .rating(reviewDTO.getRating())
                    .comment(reviewDTO.getComment())
                    .build();
            reviewRepository.save(newReview);
        }

        // ✅ Update Recipe Rating after review update
        updateRecipeRating(recipeId);
    }


    // ✅ Recalculate and update the average rating of a recipe
    private void updateRecipeRating(Long recipeId) {
        List<Review> reviews = reviewRepository.findByRecipeId(recipeId);

        double avgRating = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));

        recipe.setAverageRating(avgRating); // ✅ Now updating the rating
        recipeRepository.save(recipe); // ✅ Save updated rating
    }


    // Helper: Convert Entity to DTO
    private RecipeDTO convertToDTO(Recipe recipe) {
        return RecipeDTO.builder()
                .id(recipe.getId())
                .title(recipe.getTitle())
                .description(recipe.getDescription())
                .instructions(recipe.getInstructions())
                .authorId(recipe.getAuthorId())
                .products(recipe.getProducts().stream().map(Product::getName).collect(Collectors.toList()))
                .averageRating(recipe.getAverageRating())
                .build();
    }

}
