package com.polina.reviewservice.service;

import com.polina.reviewservice.client.RecipeClient;
import com.polina.reviewservice.client.UserClient;
import com.polina.reviewservice.entity.Review;
import com.polina.reviewservice.repository.ReviewRepository;
import com.polina.dto.RecipeDTO;
import com.polina.dto.ReviewDTO;
import com.polina.dto.ReviewEvent;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserClient userClient;
    private final RecipeClient recipeClient;
    private final KafkaTemplate<String, ReviewEvent> kafkaTemplate;

    public ReviewService(ReviewRepository reviewRepository, UserClient userClient,
                         RecipeClient recipeClient, KafkaTemplate<String, ReviewEvent> kafkaTemplate) {
        this.reviewRepository = reviewRepository;
        this.userClient = userClient;
        this.recipeClient = recipeClient;
        this.kafkaTemplate = kafkaTemplate;
    }



    public void addReview(Long recipeId, ReviewDTO reviewDTO) {
        if (!checkUserExists(reviewDTO.getUserId())) {
            throw new IllegalArgumentException("User does not exist.");
        }
        if (!checkRecipeExists(recipeId)) {
            throw new IllegalArgumentException("Recipe does not exist.");
        }

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

        double averageRating = calculateAverageRating(recipeId);

        ReviewEvent event = new ReviewEvent(recipeId, averageRating);
        kafkaTemplate.send("review-events", event);
    }


    public List<Review> getReviewsForRecipe(Long recipeId) {
        return reviewRepository.findByRecipeId(recipeId);
    }

    public boolean checkUserExists(Long userId) {
        try {
            ResponseEntity<String> response = userClient.getUserById(userId);
            return response.getBody() != null;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean checkRecipeExists(Long recipeId) {
        try {
            ResponseEntity<RecipeDTO> response = recipeClient.getRecipeById(recipeId);
            return response.getBody() != null;
        } catch (Exception e) {
            return false;
        }
    }

    public double calculateAverageRating(Long recipeId) {
        List<Review> reviews = reviewRepository.findByRecipeId(recipeId);
        return reviews.isEmpty() ? 0.0 :
                reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
    }

}
