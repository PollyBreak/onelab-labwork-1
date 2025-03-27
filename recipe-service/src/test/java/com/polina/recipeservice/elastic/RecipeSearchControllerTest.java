package com.polina.recipeservice.elastic;

import com.polina.recipeservice.elasticsearch.RecipeDocument;
import com.polina.recipeservice.elasticsearch.RecipeSearchController;
import com.polina.recipeservice.elasticsearch.RecipeSearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RecipeSearchControllerTest {
    @Mock
    private RecipeSearchService recipeSearchService;
    @InjectMocks
    private RecipeSearchController recipeSearchController;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSearchRecipes() {
        Page<RecipeDocument> mockPage = new PageImpl<>(Collections.emptyList());
        when(recipeSearchService.searchRecipes
                (any(), any(), any(), any(), any(), any(), anyInt(), anyInt(), any()))
                .thenReturn(mockPage);
        ResponseEntity<Page<RecipeDocument>> response = recipeSearchController
                .searchRecipes(null, null, null, null, null, null, 0, 10, "rating");
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockPage, response.getBody());
    }

    @Test
    void testGetRecipeById_Found() {
        RecipeDocument mockRecipe = new RecipeDocument();
        when(recipeSearchService.findRecipeById("1")).thenReturn(Optional.of(mockRecipe));
        ResponseEntity<RecipeDocument> response = recipeSearchController.getRecipeById("1");
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockRecipe, response.getBody());
    }

    @Test
    void testGetRecipeById_NotFound() {
        when(recipeSearchService.findRecipeById("1")).thenReturn(Optional.empty());
        ResponseEntity<RecipeDocument> response = recipeSearchController.getRecipeById("1");
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testGetRecipesGroupedByCuisine() {
        Map<String, List<RecipeDocument>> mockMap = new HashMap<>();
        when(recipeSearchService.groupRecipesByCuisine()).thenReturn(mockMap);
        ResponseEntity<Map<String, List<RecipeDocument>>> response = recipeSearchController
                .getRecipesGroupedByCuisine();
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockMap, response.getBody());
    }
}
