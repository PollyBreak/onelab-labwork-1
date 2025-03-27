package com.polina.recipeservice.controller;

import com.polina.dto.RecipeDTO;
import com.polina.recipeservice.service.RecipeService;
import com.polina.recipeservice.service.RecommendationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.Collections;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.fasterxml.jackson.databind.ObjectMapper;

class RecipeControllerTest {
    private MockMvc mockMvc;
    @Mock
    private RecipeService recipeService;
    @Mock
    private RecommendationService recommendationService;
    @InjectMocks
    private RecipeController recipeController;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(recipeController).build();
    }

    @Test
    @WithMockUser(username = "1")
    void createRecipe_ShouldReturnSuccess() throws Exception {
        RecipeDTO recipeDTO = new RecipeDTO();
        recipeDTO.setAuthorId(1L);
        mockMvc.perform(post("/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recipeDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("Recipe created successfully"));
        verify(recipeService, times(1)).createRecipe(recipeDTO);
    }

    @Test
    @WithMockUser(username = "1")
    void updateRecipe_ShouldReturnSuccess() throws Exception {
        RecipeDTO recipeDTO = new RecipeDTO();
        recipeDTO.setAuthorId(1L);
        mockMvc.perform(put("/recipes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recipeDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("Recipe updated successfully"));
        verify(recipeService, times(1)).updateRecipe(1L, recipeDTO);
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void getUserRecommendations_ShouldReturnList() throws Exception {
        when(recommendationService.getUserRecommendations(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/recipes/recommend/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
        verify(recommendationService, times(1)).getUserRecommendations(1L);
    }

    @Test
    @WithMockUser(username = "admin")
    void syncRecipes_ShouldReturnSuccess() throws Exception {
        mockMvc.perform(get("/recipes/sync-elasticsearch"))
                .andExpect(status().isOk())
                .andExpect(content().string("Recipes successfully synchronized to Elasticsearch."));
        verify(recipeService, times(1)).syncRecipesToElasticsearch();
    }

    @Test
    @WithMockUser(username = "1")
    void deleteRecipe_ShouldReturnSuccess() throws Exception {
        mockMvc.perform(delete("/recipes/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Recipe deleted successfully"));
        verify(recipeService, times(1)).deleteRecipe(1L);
    }
}
