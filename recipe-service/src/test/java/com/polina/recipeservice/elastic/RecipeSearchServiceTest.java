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
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeSearchServiceTest {
    @Mock
    private RecipeSearchRepository recipeSearchRepository;
    @Mock
    private ElasticsearchOperations elasticsearchOperations;
    @InjectMocks
    private RecipeSearchService recipeSearchService;
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
    void saveRecipe_Success() {
        recipeSearchService.saveRecipe(recipeDocument);
        verify(recipeSearchRepository, times(1)).save(recipeDocument);
    }

    @Test
    void deleteRecipe_Success() {
        recipeSearchService.deleteRecipe("1");
        verify(recipeSearchRepository, times(1)).deleteById("1");
    }

    @Test
    void getAllRecipes_ReturnsList() {
        when(recipeSearchRepository.findAll()).thenReturn(List.of(recipeDocument));
        List<RecipeDocument> result = recipeSearchService.getAllRecipes();
        assertEquals(1, result.size());
        assertEquals("Pasta", result.get(0).getTitle());
    }

    @Test
    void filterRecipesByTitle_ReturnsMatchingRecipes() {
        SearchHit<RecipeDocument> searchHit = mock(SearchHit.class);
        when(searchHit.getContent()).thenReturn(recipeDocument);
        SearchHits<RecipeDocument> searchHits = mock(SearchHits.class);
        when(searchHits.getSearchHits()).thenReturn(List.of(searchHit));
        when(elasticsearchOperations.search(any(CriteriaQuery.class), eq(RecipeDocument.class)))
                .thenReturn(searchHits);
        List<RecipeDocument> result = recipeSearchService.filterRecipesByTitle("Pasta");
        assertEquals(1, result.size());
        assertEquals("Pasta", result.get(0).getTitle());
    }

    @Test
    void filterRecipesByCuisineAndRating_WithCuisineOnly() {
        SearchHit<RecipeDocument> searchHit = mock(SearchHit.class);
        when(searchHit.getContent()).thenReturn(recipeDocument);
        SearchHits<RecipeDocument> searchHits = mock(SearchHits.class);
        when(searchHits.getSearchHits()).thenReturn(List.of(searchHit));
        when(elasticsearchOperations.search(any(CriteriaQuery.class), eq(RecipeDocument.class)))
                .thenReturn(searchHits);
        List<RecipeDocument> result = recipeSearchService
                .filterRecipesByCuisineAndRating("Italian", null);
        assertEquals(1, result.size());
        assertEquals("Italian", result.get(0).getCuisine());
    }

    @Test
    void filterRecipesByCuisineAndRating_WithMinRatingOnly() {
        SearchHit<RecipeDocument> searchHit = mock(SearchHit.class);
        when(searchHit.getContent()).thenReturn(recipeDocument);
        SearchHits<RecipeDocument> searchHits = mock(SearchHits.class);
        when(searchHits.getSearchHits()).thenReturn(List.of(searchHit));
        when(elasticsearchOperations.search(any(CriteriaQuery.class), eq(RecipeDocument.class)))
                .thenReturn(searchHits);
        List<RecipeDocument> result = recipeSearchService
                .filterRecipesByCuisineAndRating(null, 4.0);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getAverageRating() >= 4.0);
    }

    @Test
    void filterRecipesByCuisineAndRating_WithBothFilters() {
        SearchHit<RecipeDocument> searchHit = mock(SearchHit.class);
        when(searchHit.getContent()).thenReturn(recipeDocument);
        SearchHits<RecipeDocument> searchHits = mock(SearchHits.class);
        when(searchHits.getSearchHits()).thenReturn(List.of(searchHit));
        when(elasticsearchOperations.search(any(CriteriaQuery.class), eq(RecipeDocument.class)))
                .thenReturn(searchHits);
        List<RecipeDocument> result = recipeSearchService
                .filterRecipesByCuisineAndRating("Italian", 4.0);
        assertEquals(1, result.size());
        assertEquals("Italian", result.get(0).getCuisine());
        assertTrue(result.get(0).getAverageRating() >= 4.0);
    }
}
