package com.polina.recipeservice.elastic;

import com.polina.recipeservice.elasticsearch.RecipeDocument;
import com.polina.recipeservice.elasticsearch.RecipeSearchRepository;
import com.polina.recipeservice.elasticsearch.RecipeSearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeSearchServiceTest {
    @Mock
    private ElasticsearchOperations elasticsearchOperations;
    @Mock
    private RecipeSearchRepository recipeSearchRepository;
    @InjectMocks
    private RecipeSearchService recipeSearchService;

    private RecipeDocument mockRecipe;

    @BeforeEach
    void setUp() {
        mockRecipe = new RecipeDocument();
        mockRecipe.setId("1");
        mockRecipe.setCuisine("Italian");
    }

    @Test
    void findRecipeById_Found() {
        when(recipeSearchRepository.findById("1")).thenReturn(Optional.of(mockRecipe));
        Optional<RecipeDocument> result = recipeSearchService.findRecipeById("1");
        assertTrue(result.isPresent());
        assertEquals("1", result.get().getId());
    }

    @Test
    void findRecipeById_NotFound() {
        when(recipeSearchRepository.findById("1")).thenReturn(Optional.empty());
        Optional<RecipeDocument> result = recipeSearchService.findRecipeById("1");
        assertFalse(result.isPresent());
    }

    @Test
    void searchRecipes_NoFilters() {
        Page<RecipeDocument> expectedPage = new PageImpl<>(List.of(mockRecipe));
        when(elasticsearchOperations.search(any(CriteriaQuery.class), eq(RecipeDocument.class)))
                .thenReturn(mock(SearchHits.class));
        Page<RecipeDocument> result = recipeSearchService.searchRecipes(null, null, null, null, null, null, 0, 10, "rating");
        assertNotNull(result);
    }

    @Test
    void searchRecipes_WithFilters() {
        Page<RecipeDocument> expectedPage = new PageImpl<>(List.of(mockRecipe));
        when(elasticsearchOperations.search(any(CriteriaQuery.class), eq(RecipeDocument.class)))
                .thenReturn(mock(SearchHits.class));
        Page<RecipeDocument> result = recipeSearchService
                .searchRecipes("1", "Pasta", "Italian", 4.0, "Delicious", List.of("Tomato"), 0, 10, "newest");
        assertNotNull(result);
    }

    @Test
    void groupRecipesByCuisine() {
        SearchHits<RecipeDocument> searchHits = mock(SearchHits.class);
        when(searchHits.getSearchHits()).thenReturn(Collections.emptyList());
        when(elasticsearchOperations.search(any(CriteriaQuery.class), eq(RecipeDocument.class)))
                .thenReturn(searchHits);
        Map<String, List<RecipeDocument>> result = recipeSearchService.groupRecipesByCuisine();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
