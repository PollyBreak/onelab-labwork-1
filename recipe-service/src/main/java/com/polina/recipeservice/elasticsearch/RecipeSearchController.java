package com.polina.recipeservice.elasticsearch;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;


@RestController
@RequestMapping("/recipes")
public class RecipeSearchController {
    private final RecipeSearchService recipeSearchService;

    public RecipeSearchController(RecipeSearchService recipeSearchService) {
        this.recipeSearchService = recipeSearchService;
    }

    @GetMapping()
    public ResponseEntity<Page<RecipeDocument>> searchRecipes(
            @RequestParam(required = false) String authorId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String cuisine,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) List<String> products,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "rating") String sortBy) {

        Page<RecipeDocument> results = recipeSearchService.searchRecipes(
                authorId, title, cuisine, minRating, description, products, page, size, sortBy
        );
        return ResponseEntity.ok(results);
    }


    @GetMapping("/{id}")
    public ResponseEntity<RecipeDocument> getRecipeById(@PathVariable String id) {
        return recipeSearchService.findRecipeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cuisines")
    public ResponseEntity<Map<String, List<RecipeDocument>>> getRecipesGroupedByCuisine() {
        return ResponseEntity.ok(recipeSearchService.groupRecipesByCuisine());
    }

}
