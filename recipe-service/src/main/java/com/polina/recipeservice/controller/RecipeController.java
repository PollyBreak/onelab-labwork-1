package com.polina.recipeservice.controller;

import com.polina.recipeservice.dto.RecipeDTO;
import com.polina.recipeservice.dto.ReviewDTO;
import com.polina.recipeservice.service.RecipeService;
import com.polina.recipeservice.service.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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

//    @GetMapping
//    public ResponseEntity<List<RecipeDTO>> getAllRecipes() {
//        return ResponseEntity.ok(recipeService.getAllRecipes());
//    }

    @GetMapping
    public ResponseEntity<List<RecipeDTO>> getRecipes(
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) String cuisine,
            @RequestParam(required = false) List<String> products,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) String newerThan) {
        LocalDateTime dateFilter = (newerThan != null) ? LocalDateTime.parse(newerThan) : null;
        return ResponseEntity.ok(recipeService.findRecipes(authorId, cuisine, products, minRating,
                dateFilter));
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


    @GetMapping("/cuisines")
    public ResponseEntity<Map<String, List<RecipeDTO>>> getRecipesGroupedByCuisine() {
        return ResponseEntity.ok(recipeService.groupRecipesByCuisine());
    }

    @GetMapping("/products/count")
    public ResponseEntity<Map<Integer, List<RecipeDTO>>> getRecipesGroupedByProductCount() {
        return ResponseEntity.ok(recipeService.groupRecipesByProductCount());
    }

    @GetMapping("/ratings/partition")
    public ResponseEntity<Map<Boolean, List<RecipeDTO>>> getRecipesPartitionedByRating(@RequestParam double threshold) {
        return ResponseEntity.ok(recipeService.partitionRecipesByRating(threshold));
    }

    @GetMapping("/performance/comparison")
    public ResponseEntity<Map<String, Double>> compareStreamPerformance() {
        return ResponseEntity.ok(recipeService.compareSequentialVsParallelProcessing());
    }


    @GetMapping("/sync-elasticsearch")
    public ResponseEntity<String> syncRecipes() {
        recipeService.syncRecipesToElasticsearch();
        return ResponseEntity.ok("Recipes successfully synchronized to Elasticsearch.");
    }


}
