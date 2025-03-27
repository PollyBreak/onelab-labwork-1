package com.polina.recipeservice.service;


import com.polina.dto.RecipeDTO;
import com.polina.recipeservice.elasticsearch.RecipeDocument;
import com.polina.recipeservice.elasticsearch.RecipeSearchRepository;
import com.polina.recipeservice.entity.Product;
import com.polina.recipeservice.entity.Recipe;
import com.polina.recipeservice.entity.UserRecommendation;
import com.polina.recipeservice.repository.ProductRepository;
import com.polina.recipeservice.repository.RecipeRepository;
import com.polina.recipeservice.repository.UserRecommendationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RecipeServiceTest {
    @Mock
    private RecipeRepository recipeRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private RecipeSearchRepository recipeSearchRepository;
    @Mock
    private UserRecommendationRepository userRecommendationRepository;

    @InjectMocks
    private RecipeService recipeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createRecipe_Success() {
        RecipeDTO recipeDTO = RecipeDTO.builder()
                .title("Test Recipe")
                .description("Test Description")
                .instructions("Test Instructions")
                .authorId(1L)
                .products(List.of("Tomato", "Cheese"))
                .build();
        Recipe savedRecipe = Recipe.builder()
                .id(1L)
                .title(recipeDTO.getTitle())
                .description(recipeDTO.getDescription())
                .instructions(recipeDTO.getInstructions())
                .authorId(recipeDTO.getAuthorId())
                .products(List.of(new Product(1L, "Tomato"), new Product(2L, "Cheese")))
                .createdAt(LocalDateTime.now())
                .build();

        when(recipeRepository.save(any(Recipe.class))).thenAnswer(invocation -> {
            Recipe recipe = invocation.getArgument(0);
            recipe.setId(1L);
            return recipe;
        });
        recipeService.createRecipe(recipeDTO);
        verify(recipeRepository, times(1)).save(any(Recipe.class));
        verify(recipeSearchRepository, times(1)).save(any(RecipeDocument.class));
    }



    @Test
    void updateRecipe_RecipeNotFound_ThrowsException() {
        Long recipeId = 1L;
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

        RecipeDTO updatedRecipeDTO = new RecipeDTO();
        updatedRecipeDTO.setTitle("Updated Recipe");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            recipeService.updateRecipe(recipeId, updatedRecipeDTO);
        });

        assertEquals("Recipe with ID " + recipeId + " not found", exception.getMessage());
    }

    @Test
    void deleteRecipe_Success() {
        Long recipeId = 1L;
        Recipe recipe = new Recipe();
        recipe.setId(recipeId);

        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));

        recipeService.deleteRecipe(recipeId);

        verify(recipeRepository, times(1)).delete(recipe);
        verify(recipeSearchRepository, times(1)).deleteById(recipeId.toString());
    }

    @Test
    void deleteRecipe_RecipeNotFound_ThrowsException() {
        Long recipeId = 1L;
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            recipeService.deleteRecipe(recipeId);
        });

        assertEquals("Recipe with ID " + recipeId + " not found", exception.getMessage());
    }

    @Test
    void deleteRecipesByAuthor_Success() {
        Long authorId = 1L;
        Recipe recipe = new Recipe();
        recipe.setId(1L);

        when(recipeRepository.findByAuthorId(authorId)).thenReturn(List.of(recipe));

        recipeService.deleteRecipesByAuthor(authorId);

        verify(recipeRepository, times(1)).deleteByAuthorId(authorId);
        verify(recipeSearchRepository, times(1)).deleteByAuthorId(authorId);
    }

    @Test
    void syncRecipesToElasticsearch_Success() {
        Recipe recipe = new Recipe();
        recipe.setId(1L);
        recipe.setAuthorId(1L);
        recipe.setProducts(new ArrayList<>());
        when(recipeRepository.findAll()).thenReturn(List.of(recipe));
        recipeService.syncRecipesToElasticsearch();
        verify(recipeSearchRepository, times(1)).saveAll(any());
    }



    @Test
    void convertToDTO_Success() {
        Recipe recipe = new Recipe();
        recipe.setId(1L);
        recipe.setTitle("Pasta");
        recipe.setDescription("Delicious pasta");
        recipe.setInstructions("Boil water, cook pasta");
        recipe.setCuisine("Italian");
        recipe.setCreatedAt(LocalDateTime.now());
        recipe.setProducts(List.of(new Product("Pasta"), new Product("Tomato")));

        RecipeDTO dto = recipeService.convertToDTO(recipe);

        assertEquals(recipe.getId(), dto.getId());
        assertEquals(recipe.getTitle(), dto.getTitle());
        assertEquals(recipe.getDescription(), dto.getDescription());
        assertEquals(recipe.getCuisine(), dto.getCuisine());
        assertEquals(recipe.getProducts().size(), dto.getProducts().size());
    }

    @Test
    void updateUserRecommendations_Success() {
        Recipe recipe = new Recipe();
        recipe.setId(1L);
        recipe.setProducts(List.of(new Product("Tomato")));
        Recipe existingRecommendedRecipe = new Recipe();
        existingRecommendedRecipe.setId(2L);
        existingRecommendedRecipe.setProducts(List.of(new Product("Tomato")));
        UserRecommendation recommendation = new UserRecommendation();
        recommendation.setRecommendedRecipes(new ArrayList<>(List.of(existingRecommendedRecipe)));

        when(userRecommendationRepository.findAll()).thenReturn(List.of(recommendation));
        recipeService.updateUserRecommendations(recipe);
        verify(userRecommendationRepository, times(1))
                .save(any(UserRecommendation.class));
    }



}
