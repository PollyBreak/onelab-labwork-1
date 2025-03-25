package com.polina.recipeservice.service;

import com.polina.recipeservice.entity.Recipe;
import com.polina.recipeservice.repository.UserRecommendationRepository;
import com.polina.dto.RecipeDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final UserRecommendationRepository userRecommendationRepository;

    public RecommendationService(UserRecommendationRepository userRecommendationRepository) {
        this.userRecommendationRepository = userRecommendationRepository;
    }


    public List<RecipeDTO> getUserRecommendations(Long userId) {
        return userRecommendationRepository.findById(userId)
                .map(recommendation -> recommendation.getRecommendedRecipes().stream()
                        .map(this::convertToDTO)
                        .collect(Collectors.toList())
                )
                .orElse(List.of());
    }

    private RecipeDTO convertToDTO(Recipe recipe) {
        return RecipeDTO.builder()
                .id(recipe.getId())
                .title(recipe.getTitle())
                .description(recipe.getDescription())
                .instructions(recipe.getInstructions())
                .authorId(recipe.getAuthorId())
                .products(recipe.getProducts()
                        .stream()
                        .map(product -> product.getName()).collect(Collectors.toList()))
                .averageRating(recipe.getAverageRating())
                .build();
    }
}
