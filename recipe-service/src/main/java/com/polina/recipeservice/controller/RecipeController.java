package com.polina.recipeservice.controller;

import com.polina.recipeservice.service.RecipeService;
import com.polina.recipeservice.service.RecommendationService;
import com.polina.dto.RecipeDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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


    @PostMapping
    @PreAuthorize("#recipeDTO.authorId == authentication.principal")
    public ResponseEntity<String> createRecipe(@RequestBody RecipeDTO recipeDTO) {
        recipeService.createRecipe(recipeDTO);
        return ResponseEntity.ok("Recipe created successfully");
    }

    @PutMapping("/{recipeId}")
    @PreAuthorize("#recipeDto.authorId == authentication.principal")
    public ResponseEntity<String> updateRecipe(@PathVariable Long recipeId, @RequestBody RecipeDTO recipeDTO) {
        recipeService.updateRecipe(recipeId, recipeDTO);
        return ResponseEntity.ok("Recipe updated successfully");
    }

    @DeleteMapping("/{recipeId}")
    @PreAuthorize("@recipeSecurityService.isOwner(#recipeId, authentication.principal)")
    public ResponseEntity<String> deleteRecipe(@PathVariable Long recipeId) {
        recipeService.deleteRecipe(recipeId);
        return ResponseEntity.ok("Recipe deleted successfully");
    }


    @GetMapping("/recommend/{userId}")
    @PreAuthorize("#userId == authentication.principal or hasAuthority('ADMIN')")
    public ResponseEntity<List<RecipeDTO>> getUserRecommendations(@PathVariable Long userId) {
        List<RecipeDTO> recommendations = recommendationService.getUserRecommendations(userId);
        return ResponseEntity.ok(recommendations);
    }



    @GetMapping("/sync-elasticsearch")
    public ResponseEntity<String> syncRecipes() {
        recipeService.syncRecipesToElasticsearch();
        return ResponseEntity.ok("Recipes successfully synchronized to Elasticsearch.");
    }


}
