package com.polina.recipeservice.elasticsearch;


import com.polina.recipeservice.elasticsearch.RecipeDocument;
import com.polina.recipeservice.elasticsearch.RecipeSearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recipes/search")
public class RecipeSearchController {
    private final RecipeSearchService recipeSearchService;

    public RecipeSearchController(RecipeSearchService recipeSearchService) {
        this.recipeSearchService = recipeSearchService;
    }

    @GetMapping("/by-title")
    public ResponseEntity<List<RecipeDocument>> searchByTitle(@RequestParam String title) {
        return ResponseEntity.ok(recipeSearchService.filterRecipesByTitle(title));
    }

    @GetMapping("/by-cuisine-rating")
    public ResponseEntity<List<RecipeDocument>> searchByCuisineAndRating(
            @RequestParam(required = false) String cuisine,
            @RequestParam(required = false) Double minRating) {
        return ResponseEntity.ok(recipeSearchService.filterRecipesByCuisineAndRating(cuisine, minRating));
    }
}
