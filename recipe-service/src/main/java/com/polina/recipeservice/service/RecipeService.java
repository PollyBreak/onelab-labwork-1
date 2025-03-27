package com.polina.recipeservice.service;

import com.polina.recipeservice.elasticsearch.RecipeDocument;
import com.polina.recipeservice.elasticsearch.RecipeSearchRepository;
import com.polina.recipeservice.entity.Product;
import com.polina.recipeservice.entity.Recipe;
import com.polina.recipeservice.repository.ProductRepository;
import com.polina.recipeservice.repository.RecipeRepository;
import com.polina.recipeservice.repository.UserRecommendationRepository;
import com.polina.dto.RecipeDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final ProductRepository productRepository;
    private final RecipeSearchRepository recipeSearchRepository;
    private final UserRecommendationRepository userRecommendationRepository;

    public RecipeService(RecipeRepository recipeRepository,
                         ProductRepository productRepository,
                         RecipeSearchRepository recipeSearchRepository,
                         UserRecommendationRepository userRecommendationRepository) {
        this.recipeRepository = recipeRepository;
        this.productRepository = productRepository;
        this.recipeSearchRepository = recipeSearchRepository;
        this.userRecommendationRepository = userRecommendationRepository;
    }

    public void createRecipe(RecipeDTO recipeDTO) {
        List<Product> products = recipeDTO.getProducts().stream()
                .map(name -> productRepository
                        .findByName(name) != null ?
                        productRepository.findByName(name) :
                        productRepository.save(new Product(null, name)))
                .collect(Collectors.toList());
        Recipe recipe = Recipe.builder()
                .title(recipeDTO.getTitle()).description(recipeDTO.getDescription())
                .instructions(recipeDTO.getInstructions()).authorId(recipeDTO.getAuthorId())
                .cuisine(recipeDTO.getCuisine()).products(products)
                .createdAt(LocalDateTime.now())
                .build();
        recipeRepository.save(recipe);
        RecipeDocument recipeDocument = RecipeDocument.builder()
                .id(recipe.getId().toString())
                .authorId(recipe.getAuthorId().toString())
                .title(recipe.getTitle()).description(recipe.getDescription()).cuisine(recipe.getCuisine())
                .products(recipeDTO.getProducts()).averageRating(recipe.getAverageRating())
                .createdAt(recipe.getCreatedAt())
                .build();
        recipeSearchRepository.save(recipeDocument);
        updateUserRecommendations(recipe);
    }

    public void updateRecipe(Long recipeId, RecipeDTO updatedRecipeDTO) {
        Recipe existingRecipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Recipe with ID " + recipeId + " not found"));

        Set<String> oldProductNames = existingRecipe.getProducts().stream()
                .map(Product::getName)
                .collect(Collectors.toSet());

        List<Product> updatedProducts = updatedRecipeDTO.getProducts().stream()
                .map(name -> productRepository.findByName(name) != null
                        ? productRepository.findByName(name)
                        : productRepository.save(new Product(null, name)))
                .collect(Collectors.toList());

        existingRecipe.setTitle(updatedRecipeDTO.getTitle());
        existingRecipe.setDescription(updatedRecipeDTO.getDescription());
        existingRecipe.setInstructions(updatedRecipeDTO.getInstructions());
        existingRecipe.setCuisine(updatedRecipeDTO.getCuisine());
        existingRecipe.setProducts(updatedProducts);
        existingRecipe.setCreatedAt(LocalDateTime.now());
        recipeRepository.save(existingRecipe);

        syncRecipeToElasticsearch(existingRecipe);

        Set<String> newProductNames = updatedProducts.stream()
                .map(Product::getName)
                .collect(Collectors.toSet());

        if (!oldProductNames.equals(newProductNames)) {
            updateUserRecommendations(existingRecipe);
        }
    }

    public void deleteRecipe(Long recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("Recipe with ID " + recipeId + " not found"));

        userRecommendationRepository.findAll().forEach(recommendation -> {
            recommendation.getRecommendedRecipes().removeIf(r -> r.getId().equals(recipeId));
            userRecommendationRepository.save(recommendation);
        });

        recipeRepository.delete(recipe);
        recipeSearchRepository.deleteById(recipeId.toString());
        List<Long> recipeIds = new ArrayList<>();
        recipeIds.add(recipe.getId());
    }

    public void deleteRecipesByAuthor(Long userId) {
        System.out.println("Deleting all recipes for user: " + userId);
        List<Recipe> userRecipes = recipeRepository.findByAuthorId(userId);
        for (Recipe recipe : userRecipes) {
            userRecommendationRepository.deleteByRecipeId(recipe.getId());
        }
        List<Long> recipeIds = userRecipes.stream().map(Recipe::getId).collect(Collectors.toList());

        recipeRepository.deleteByAuthorId(userId);
        recipeSearchRepository.deleteByAuthorId(userId);
    }

    public RecipeDTO convertToDTO(Recipe recipe) {
        return RecipeDTO.builder()
                .id(recipe.getId())
                .title(recipe.getTitle())
                .description(recipe.getDescription())
                .instructions(recipe.getInstructions())
                .authorId(recipe.getAuthorId())
                .cuisine(recipe.getCuisine())
                .products(recipe.getProducts().stream().map(Product::getName).
                        collect(Collectors.toList()))
                .averageRating(recipe.getAverageRating())
                .createdAt(recipe.getCreatedAt())
                .build();
    }

    private RecipeDocument convertToRecipeDocument(Recipe recipe) {
        return RecipeDocument.builder()
                .id(recipe.getId().toString())
                .authorId(recipe.getAuthorId().toString())
                .title(recipe.getTitle())
                .description(recipe.getDescription())
                .cuisine(recipe.getCuisine())
                .products(recipe.getProducts().stream().map(Product::getName).collect(Collectors.toList()))
                .averageRating(recipe.getAverageRating())
                .createdAt(recipe.getCreatedAt())
                .build();
    }

    private void syncRecipeToElasticsearch(Recipe recipe) {
        RecipeDocument recipeDocument = convertToRecipeDocument(recipe);
        recipeSearchRepository.save(recipeDocument);
    }

    public void syncRecipesToElasticsearch() {
        List<Recipe> recipes = recipeRepository.findAll();
        List<RecipeDocument> recipeDocuments = recipes.stream()
                .map(this::convertToRecipeDocument)
                .collect(Collectors.toList());
        recipeSearchRepository.saveAll(recipeDocuments);
    }

    public void updateUserRecommendations(Recipe recipe) {
        userRecommendationRepository.findAll().forEach(recommendation -> {
            if (recipe.getProducts().stream().anyMatch(product ->
                    recommendation.getRecommendedRecipes().stream()
                            .flatMap(r -> r.getProducts().stream())
                            .map(Product::getName)
                            .collect(Collectors.toSet())
                            .contains(product.getName()))) {
                if (!recommendation.getRecommendedRecipes().contains(recipe)) {
                    recommendation.getRecommendedRecipes().add(recipe);
                    userRecommendationRepository.save(recommendation);
                }
            }
        });
    }


}
