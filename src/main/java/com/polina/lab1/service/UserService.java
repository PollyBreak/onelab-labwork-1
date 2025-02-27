package com.polina.lab1.service;

import com.polina.lab1.dto.ProductDTO;
import com.polina.lab1.dto.RecipeDTO;
import com.polina.lab1.dto.UserDTO;
import com.polina.lab1.repository.ProductRepository;
import com.polina.lab1.repository.RecipeRepository;
import com.polina.lab1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;
    private final ProductRepository productRepository;

    @Autowired
    public UserService(UserRepository userRepository, RecipeRepository recipeRepository,
                       ProductRepository productRepository){
        this.userRepository=userRepository;
        this.recipeRepository=recipeRepository;
        this.productRepository=productRepository;
    }

    public void saveUser(UserDTO user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new IllegalArgumentException("Username '"+ user.getUsername() + "' is already taken!");
        }
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new IllegalArgumentException("Email '" + user.getEmail()+ "' is already registered!");
        }
        userRepository.save(user);
    }

    public UserDTO findUserById(Long id) {
        UserDTO user = userRepository.findById(id);
        if (user == null) {
            throw new NoSuchElementException("User with ID " + id + " was not found.");
        }
        return user;
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(Long id) {
        userRepository.delete(id);
    }

    public void addRecipe(Long userId, RecipeDTO recipe, List<ProductDTO> products) {
        UserDTO user = userRepository.findById(userId);
        if (user != null) {
            recipe.setAuthorId(userId);

            List<Long> productIds = new ArrayList<>();
            for (ProductDTO productDTO:products){
                ProductDTO existingProduct = productRepository.findByName(productDTO.getName());
                if (existingProduct != null) {
                    productIds.add(existingProduct.getId());
                } else {
                    productRepository.save(productDTO);
                    ProductDTO savedProduct = productRepository.findByName(productDTO.getName());
                    productIds.add(savedProduct.getId());
                }
            }
            recipe.setProductIds(productIds);

            recipeRepository.save(recipe);
            RecipeDTO savedRecipe = recipeRepository.findById(recipe.getId());
            user.getRecipesIds().add(savedRecipe.getId());
            userRepository.save(user);
        } else {
            throw new NoSuchElementException("Used witd id " + userId + " was not found.");
        }
    }

    public List<RecipeDTO> getRecipesByUser(Long userId){
        UserDTO user = userRepository.findById(userId);
        if (user != null) {
            return user.getRecipesIds()
                    .stream()
                    .map(recipeId->recipeRepository.findById(recipeId))
                    .toList();
        } else {
            throw new NoSuchElementException("User with ID " + userId + " was not found.");
        }
    }

    public ProductDTO getProductById(Long productId) {
        return productRepository.findById(productId);
    }
}
