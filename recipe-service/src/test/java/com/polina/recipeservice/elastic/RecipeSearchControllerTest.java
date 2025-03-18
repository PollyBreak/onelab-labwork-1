package com.polina.recipeservice.elastic;

import com.polina.recipeservice.elasticsearch.RecipeDocument;
import com.polina.recipeservice.elasticsearch.RecipeSearchController;
import com.polina.recipeservice.elasticsearch.RecipeSearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeSearchControllerTest {
    @Mock
    private RecipeSearchService recipeSearchService;
    @InjectMocks
    private RecipeSearchController recipeSearchController;
    private RecipeDocument recipeDocument;

    @BeforeEach
    void setUp() {
        recipeDocument = new RecipeDocument();
        recipeDocument.setId("1");
        recipeDocument.setTitle("Pasta");
        recipeDocument.setCuisine("Italian");
        recipeDocument.setAverageRating(4.5);
    }

    @Test
    void searchByTitle_ReturnsMatchingRecipes() {
        when(recipeSearchService.filterRecipesByTitle("Pasta")).thenReturn(List.of(recipeDocument));
        ResponseEntity<List<RecipeDocument>> response = recipeSearchController.searchByTitle("Pasta");
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals("Pasta", response.getBody().get(0).getTitle());
    }

    @Test
    void searchByCuisineAndRating_WithCuisineOnly() {
        when(recipeSearchService.filterRecipesByCuisineAndRating("Italian", null))
                .thenReturn(List.of(recipeDocument));
        ResponseEntity<List<RecipeDocument>> response = recipeSearchController
                .searchByCuisineAndRating("Italian", null);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals("Italian", response.getBody().get(0).getCuisine());
    }

    @Test
    void searchByCuisineAndRating_WithMinRatingOnly() {
        when(recipeSearchService.filterRecipesByCuisineAndRating(null, 4.0))
                .thenReturn(List.of(recipeDocument));
        ResponseEntity<List<RecipeDocument>> response = recipeSearchController
                .searchByCuisineAndRating(null, 4.0);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertTrue(response.getBody().get(0).getAverageRating() >= 4.0);
    }

    @Test
    void searchByCuisineAndRating_WithBothFilters() {
        when(recipeSearchService.filterRecipesByCuisineAndRating("Italian", 4.0))
                .thenReturn(List.of(recipeDocument));
        ResponseEntity<List<RecipeDocument>> response = recipeSearchController
                .searchByCuisineAndRating("Italian", 4.0);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals("Italian", response.getBody().get(0).getCuisine());
        assertTrue(response.getBody().get(0).getAverageRating() >= 4.0);
    }
}
