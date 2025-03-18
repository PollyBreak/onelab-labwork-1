package com.polina.recipeservice.service;

import com.polina.recipeservice.client.UserClient;
import com.polina.recipeservice.dto.RecipeDTO;
import com.polina.recipeservice.dto.ReviewDTO;
import com.polina.recipeservice.elasticsearch.RecipeDocument;
import com.polina.recipeservice.elasticsearch.RecipeSearchRepository;
import com.polina.recipeservice.entity.Product;
import com.polina.recipeservice.entity.Recipe;
import com.polina.recipeservice.entity.Review;
import com.polina.recipeservice.repository.ProductRepository;
import com.polina.recipeservice.repository.RecipeRepository;
import com.polina.recipeservice.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;
    private final RecipeSearchRepository recipeSearchRepository;
    private final UserClient userClient;

    public RecipeService(RecipeRepository recipeRepository, ProductRepository productRepository,
                         ReviewRepository reviewRepository,
                         RecipeSearchRepository recipeSearchRepository,
                         UserClient userClient) {
        this.recipeRepository = recipeRepository;
        this.productRepository = productRepository;
        this.reviewRepository = reviewRepository;
        this.recipeSearchRepository = recipeSearchRepository;
        this.userClient = userClient;
    }

    public void createRecipe(RecipeDTO recipeDTO) {
        try {
            userClient.checkUserExists(recipeDTO.getAuthorId());
        } catch (Exception e) {
            throw new IllegalArgumentException
                    ("User with ID " + recipeDTO.getAuthorId() + " does not exist.");
        }

        List<Product> products = recipeDTO.getProducts().stream()
                .map(name -> productRepository
                        .findByName(name) != null ?
                        productRepository.findByName(name) :
                        productRepository.save(new Product(null, name)))
                .collect(Collectors.toList());

        Recipe recipe = Recipe.builder()
                .title(recipeDTO.getTitle())
                .description(recipeDTO.getDescription())
                .instructions(recipeDTO.getInstructions())
                .authorId(recipeDTO.getAuthorId())
                .cuisine(recipeDTO.getCuisine())
                .products(products)
                .createdAt(LocalDateTime.now())
                .build();
        recipeRepository.save(recipe);

        RecipeDocument recipeDocument = RecipeDocument.builder()
                .id(recipe.getId().toString())
                .title(recipe.getTitle())
                .description(recipe.getDescription())
                .cuisine(recipe.getCuisine())
                .products(recipeDTO.getProducts())
                .averageRating(recipe.getAverageRating())
                .build();

        recipeSearchRepository.save(recipeDocument);
    }

    public void syncRecipesToElasticsearch() {
        List<Recipe> recipes = recipeRepository.findAll();

        List<RecipeDocument> recipeDocuments = recipes.stream()
                .map(recipe -> RecipeDocument.builder()
                        .id(recipe.getId().toString())
                        .title(recipe.getTitle())
                        .description(recipe.getDescription())
                        .cuisine(recipe.getCuisine())
                        .products(recipe.getProducts()
                                .stream().map(Product::getName).collect(Collectors.toList()))
                        .averageRating(recipe.getAverageRating())
                        .build())
                .collect(Collectors.toList());

        recipeSearchRepository.saveAll(recipeDocuments);
    }

    public List<RecipeDTO> getAllRecipes() {
        return recipeRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<RecipeDTO> findRecipes(Long authorId, String cuisine, List<String> productNames,
                                       Double minRating, LocalDateTime newerThan) {
        return recipeRepository.findAll().stream()
                .filter(recipe -> authorId == null || recipe.getAuthorId().equals(authorId))
                .filter(recipe -> cuisine == null || recipe.getCuisine().equalsIgnoreCase(cuisine))
                .filter(recipe -> productNames == null || recipe.getProducts().stream()
                        .map(Product::getName).collect(Collectors.toList()).containsAll(productNames))
                .filter(recipe -> minRating == null || recipe.getAverageRating() >= minRating)
                .filter(recipe -> newerThan == null || recipe.getCreatedAt().isAfter(newerThan))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<RecipeDTO> getRecipesByUser(Long userId) {
        return recipeRepository.findByAuthorId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public void addReview(Long recipeId, ReviewDTO reviewDTO) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Recipe not found with ID: "+recipeId));
        Optional<Review> existingReview = reviewRepository.
                findByRecipeIdAndUserId(recipeId, reviewDTO.getUserId());
        if (existingReview.isPresent()) {
            Review reviewToUpdate = existingReview.get();
            reviewToUpdate.setRating(reviewDTO.getRating());
            reviewToUpdate.setComment(reviewDTO.getComment());
            reviewRepository.save(reviewToUpdate);
        } else {
            Review newReview = Review.builder()
                    .recipeId(recipeId)
                    .userId(reviewDTO.getUserId())
                    .rating(reviewDTO.getRating())
                    .comment(reviewDTO.getComment())
                    .build();
            reviewRepository.save(newReview);
        }
        updateRecipeRating(recipeId);
    }


    private void updateRecipeRating(Long recipeId) {
        List<Review> reviews = reviewRepository.findByRecipeId(recipeId);
        RatingCalculator avgRatingCalculator = (reviewList) ->
                reviewList.isEmpty() ? 0.0 :
                        reviewList.stream().map(Review::getRating).
                                reduce(0, Integer::sum) / (double) reviewList.size();
        double avgRating = avgRatingCalculator.calculate(reviews);
        recipeRepository.findById(recipeId)
                .ifPresent(recipe -> {
                    recipe.setAverageRating(avgRating);
                    recipeRepository.save(recipe);
                });
    }



    public Map<String, List<RecipeDTO>> groupRecipesByCuisine() {
        return recipeRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.groupingBy(RecipeDTO::getCuisine));
    }

    public Map<Boolean, List<RecipeDTO>> partitionRecipesByRating(double threshold) {
        return recipeRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.partitioningBy(recipe -> recipe.getAverageRating() >= threshold));
    }

    public Map<Integer, List<RecipeDTO>> groupRecipesByProductCount() {
        return recipeRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.groupingBy(recipe -> recipe.getProducts().size()));
    }


    public Map<String, Double> compareSequentialVsParallelProcessing() {
        List<Recipe> recipes = recipeRepository.findAll();
        long startSequential = System.nanoTime();
        IntStream.range(0, 1000).forEach(i ->
                recipes.stream().map(Recipe::getTitle).count()
        );
        long endSequential = System.nanoTime();
        long startParallel = System.nanoTime();
        IntStream.range(0, 1000).forEach(i ->
                recipes.parallelStream().map(Recipe::getTitle).count()
        );
        long endParallel = System.nanoTime();
        double sequentialTime = (endSequential - startSequential)/1e6;
        double parallelTime = (endParallel - startParallel)/1e6;

        Map<String, Double> results = new HashMap<>();
        results.put("Sequential Execution Time (ms)", sequentialTime);
        results.put("Parallel Execution Time (ms)", parallelTime);
        return results;
    }


    public RecipeDTO convertToDTO(Recipe recipe) {
        return RecipeDTO.builder()
                .id(recipe.getId())
                .title(recipe.getTitle())
                .description(recipe.getDescription())
                .instructions(recipe.getInstructions())
                .authorId(recipe.getAuthorId())
                .cuisine(recipe.getCuisine())
                .products(recipe.getProducts().stream().map(Product::getName).
                        collect(Collectors.toList()))
                .averageRating(recipe.getAverageRating())
                .createdAt(recipe.getCreatedAt())
                .build();
    }

}
