package com.polina.lab1.service;

import com.polina.lab1.dto.ProductDTO;
import com.polina.lab1.dto.RecipeDTO;
import com.polina.lab1.dto.UserDTO;
import com.polina.lab1.repository.ProductRepository;
import com.polina.lab1.repository.RecipeRepository;
import com.polina.lab1.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {
    @Mock
    private RecipeRepository recipeRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private RecipeService recipeService;

    private UserDTO user;
    private RecipeDTO recipe;
    private ProductDTO product;

    @BeforeEach
    void setUp() {
        user = new UserDTO();
        user.setId(1L);
        user.setRecipes(new ArrayList<>());

        recipe = new RecipeDTO();
        recipe.setId(1L);
        recipe.setAuthorId(1L);

        product = new ProductDTO();
        product.setId(1L);
        product.setName("Salt");
    }

    @Test
    void addRecipe_ShouldSaveRecipe_WhenUserExists() {
        List<ProductDTO> products = List.of(product);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findByName("Salt")).thenReturn(Optional.empty());
        when(productRepository.save(any())).thenReturn(product);
        when(recipeRepository.save(any())).thenReturn(recipe);

        recipeService.addRecipe(1L, recipe, products);

        assertEquals(1, user.getRecipes().size());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void addRecipe_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        Exception exception = assertThrows(NoSuchElementException.class, () ->
                recipeService.addRecipe(1L, recipe, List.of(product))
        );
        assertEquals("User with ID 1 was not found.", exception.getMessage());
    }

    @Test
    void getRecipesByUser_ShouldReturnRecipes_WhenUserExists() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(recipeRepository.findByAuthorId(1L)).thenReturn(List.of(recipe));

        List<RecipeDTO> recipes = recipeService.getRecipesByUser(1L);

        assertEquals(1, recipes.size());
    }

    @Test
    void getRecipesByUser_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);
        Exception exception = assertThrows(NoSuchElementException.class, () ->
                recipeService.getRecipesByUser(1L)
        );
        assertEquals("User with ID 1 was not found.", exception.getMessage());
    }

    @Test
    void getProductById_ShouldReturnProduct_WhenProductExists() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        ProductDTO result = recipeService.getProductById(1L);
        assertEquals(product, result);
    }

    @Test
    void getProductById_ShouldThrowException_WhenProductNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        Exception exception = assertThrows(NoSuchElementException.class, () ->
                recipeService.getProductById(1L)
        );
        assertEquals("Product with ID 1 was not found.", exception.getMessage());
    }

    @Test
    void getAllRecipes_ShouldReturnListOfRecipes() {
        when(recipeRepository.findAll()).thenReturn(List.of(recipe));
        List<RecipeDTO> recipes = recipeService.getAllRecipes();
        assertEquals(1, recipes.size());
    }
}
