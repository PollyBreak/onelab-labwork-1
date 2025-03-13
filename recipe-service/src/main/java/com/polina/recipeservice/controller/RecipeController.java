package com.polina.recipeservice.controller;

import com.polina.recipeservice.dto.RecipeDTO;
import com.polina.recipeservice.dto.ReviewDTO;
import com.polina.recipeservice.service.RecipeService;
import com.polina.recipeservice.service.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recipes")
public class RecipeController {

    private final RecipeService recipeService;
    private final RecommendationService recommendationService;

    public RecipeController(RecipeService recipeService,
                            RecommendationService recommendationService) {
        this.recipeService = recipeService;
        this.recommendationService = recommendationService;
    }

//    @PostMapping
//    public ResponseEntity<String> createRecipe(@RequestBody RecipeDTO recipeDTO) {
//        recipeService.createRecipe(recipeDTO);
//        return ResponseEntity.ok("Recipe created successfully");
//    }
    @PostMapping
    public ResponseEntity<String> createRecipe(@RequestBody RecipeDTO recipeDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        Long userId = (Long) authentication.getPrincipal();
        if (!recipeDTO.getAuthorId().equals(userId)) {
            return ResponseEntity.status(403).body("Forbidden: You can only create recipes for yourself");
        }
        recipeService.createRecipe(recipeDTO);
        return ResponseEntity.ok("Recipe created successfully");
    }

    @GetMapping
    public ResponseEntity<List<RecipeDTO>> getAllRecipes() {
        return ResponseEntity.ok(recipeService.getAllRecipes());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RecipeDTO>> getRecipesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(recipeService.getRecipesByUser(userId));
    }


    @GetMapping("/recommend/{userId}")
    public ResponseEntity<List<RecipeDTO>> getUserRecommendations(@PathVariable Long userId) {
        List<RecipeDTO> recommendations = recommendationService.getUserRecommendations(userId);
        return ResponseEntity.ok(recommendations);
    }

    @PostMapping("/{recipeId}/review")
    public ResponseEntity<String> addReview(@PathVariable Long recipeId, @RequestBody ReviewDTO reviewDTO) {
        recipeService.addReview(recipeId, reviewDTO);
        return ResponseEntity.ok("Review added successfully");
    }


}
