package com.polina.lab1.service;

import com.polina.lab1.dto.ProductDTO;
import com.polina.lab1.dto.RecipeDTO;
import com.polina.lab1.dto.UserDTO;
import com.polina.lab1.repository.ProductRepository;
import com.polina.lab1.repository.RecipeRepository;
import com.polina.lab1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Transactional
@Service
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Autowired
    public RecipeService(RecipeRepository recipeRepository,
                         UserRepository userRepository,
                         ProductRepository productRepository){
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    public void addRecipe(Long userId, RecipeDTO recipe, List<ProductDTO> products) {
        UserDTO user = userRepository.findById(userId)
                .orElseThrow(()-> new NoSuchElementException("User with ID " + userId + " was not found."));

        recipe.setAuthorId(userId);
        List<ProductDTO> attachedProducts = new ArrayList<>();

        for (ProductDTO productDTO : products) {
            ProductDTO existingProduct = productRepository.findByName(productDTO.getName()).orElse(null);
            if (existingProduct == null) {
                existingProduct = productRepository.save(productDTO);
            }
            attachedProducts.add(existingProduct);
        }

        recipe.setProducts(attachedProducts);
        RecipeDTO savedRecipe = recipeRepository.save(recipe);

        user.getRecipes().add(savedRecipe);
        userRepository.save(user);
    }

    public List<RecipeDTO> getRecipesByUser(Long userId){
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User with ID " + userId + " was not found.");
        }
        return recipeRepository.findByAuthorId(userId);
    }

    public ProductDTO getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(()->new NoSuchElementException("Product with ID " + productId + " was not found."));
    }

    public List<RecipeDTO> getAllRecipes(){
        return recipeRepository.findAll();
    }

}
