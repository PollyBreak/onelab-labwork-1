package com.polina.reviewservice.service;

import com.polina.reviewservice.client.RecipeClient;
import com.polina.reviewservice.client.UserClient;
import com.polina.reviewservice.entity.Review;
import com.polina.reviewservice.repository.ReviewRepository;
import com.polina.dto.RecipeDTO;
import com.polina.dto.ReviewDTO;
import com.polina.dto.ReviewEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReviewServiceTest {
    private ReviewRepository reviewRepository;
    private UserClient userClient;
    private RecipeClient recipeClient;
    private KafkaTemplate<String, ReviewEvent> kafkaTemplate;
    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        reviewRepository = mock(ReviewRepository.class);
        userClient = mock(UserClient.class);
        recipeClient = mock(RecipeClient.class);
        kafkaTemplate = mock(KafkaTemplate.class);
        reviewService = new ReviewService(reviewRepository, userClient, recipeClient, kafkaTemplate);
    }

    @Test
    void addReview_ShouldSaveNewReview_WhenUserAndRecipeExist() {
        Long recipeId = 1L;
        Long userId = 2L;
        ReviewDTO reviewDTO = ReviewDTO.builder()
                .userId(userId)
                .rating(5)
                .comment("Great!")
                .build();
        when(userClient.getUserById(userId)).thenReturn(ResponseEntity.ok("User exists"));
        when(recipeClient.getRecipeById(recipeId)).thenReturn(ResponseEntity.ok(RecipeDTO.builder().build()));
        when(reviewRepository.findByRecipeIdAndUserId(recipeId, userId)).thenReturn(Optional.empty());
        when(reviewRepository.findByRecipeId(recipeId)).thenReturn(List.of());
        reviewService.addReview(recipeId, reviewDTO);
        verify(reviewRepository).save(argThat(review ->
                review.getRecipeId().equals(recipeId) &&
                        review.getUserId().equals(userId) &&
                        review.getRating() == 5 &&
                        review.getComment().equals("Great!")
        ));

        ArgumentCaptor<ReviewEvent> eventCaptor = ArgumentCaptor.forClass(ReviewEvent.class);
        verify(kafkaTemplate).send(eq("review-events"), eventCaptor.capture());
        assertEquals(recipeId, eventCaptor.getValue().getRecipeId());
        assertEquals(0.0, eventCaptor.getValue().getAverageRating());
    }

    @Test
    void addReview_ShouldUpdateExistingReview() {
        Long recipeId = 1L;
        Long userId = 2L;
        ReviewDTO reviewDTO = ReviewDTO.builder()
                .userId(userId)
                .rating(4)
                .comment("Updated review")
                .build();
        Review existingReview = Review.builder()
                .recipeId(recipeId)
                .userId(userId)
                .rating(3)
                .comment("Old review")
                .build();

        when(userClient.getUserById(userId)).thenReturn(ResponseEntity.ok("User exists"));
        when(recipeClient.getRecipeById(recipeId)).thenReturn(ResponseEntity.ok(RecipeDTO.builder().build()));
        when(reviewRepository.findByRecipeIdAndUserId(recipeId, userId)).thenReturn(Optional.of(existingReview));
        when(reviewRepository.findByRecipeId(recipeId)).thenReturn(List.of(existingReview));

        reviewService.addReview(recipeId, reviewDTO);

        verify(reviewRepository).save(argThat(review ->
                review.getRecipeId().equals(recipeId) &&
                        review.getUserId().equals(userId) &&
                        review.getRating() == 4 &&
                        review.getComment().equals("Updated review")
        ));
    }

    @Test
    void addReview_ShouldThrowException_WhenUserDoesNotExist() {
        Long recipeId = 1L;
        ReviewDTO reviewDTO = ReviewDTO.builder().userId(2L).rating(5).comment("Review").build();
        when(userClient.getUserById(2L)).thenReturn(ResponseEntity.of(Optional.empty()));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reviewService.addReview(recipeId, reviewDTO));
        assertEquals("User does not exist.", exception.getMessage());
    }

    @Test
    void addReview_ShouldThrowException_WhenRecipeDoesNotExist() {
        Long recipeId = 1L;
        ReviewDTO reviewDTO = ReviewDTO.builder().userId(2L).rating(5).comment("Review").build();
        when(userClient.getUserById(2L)).thenReturn(ResponseEntity.ok("User exists"));
        when(recipeClient.getRecipeById(recipeId)).thenReturn(ResponseEntity.of(Optional.empty()));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reviewService.addReview(recipeId, reviewDTO));
        assertEquals("Recipe does not exist.", exception.getMessage());
    }

    @Test
    void getReviewsForRecipe_ShouldReturnReviews() {
        Long recipeId = 1L;
        Review review1 = Review.builder().recipeId(recipeId).userId(2L).rating(5).comment("Nice").build();
        Review review2 = Review.builder().recipeId(recipeId).userId(3L).rating(3).comment("Okay").build();
        when(reviewRepository.findByRecipeId(recipeId)).thenReturn(List.of(review1, review2));
        List<Review> reviews = reviewService.getReviewsForRecipe(recipeId);
        assertEquals(2, reviews.size());
        assertEquals("Nice", reviews.get(0).getComment());
        assertEquals("Okay", reviews.get(1).getComment());
    }

    @Test
    void checkUserExists_ShouldReturnTrue_WhenUserExists() {
        when(userClient.getUserById(1L)).thenReturn(ResponseEntity.ok("User exists"));
        boolean exists = reviewService.checkUserExists(1L);
        assertTrue(exists);
    }

    @Test
    void checkUserExists_ShouldReturnFalse_WhenUserNotFound() {
        when(userClient.getUserById(1L)).thenThrow(new RuntimeException());
        boolean exists = reviewService.checkUserExists(1L);
        assertFalse(exists);
    }

    @Test
    void checkRecipeExists_ShouldReturnTrue_WhenRecipeExists() {
        when(recipeClient.getRecipeById(1L))
                .thenReturn(ResponseEntity.ok(RecipeDTO.builder().build()));
        boolean exists = reviewService.checkRecipeExists(1L);
        assertTrue(exists);
    }

    @Test
    void checkRecipeExists_ShouldReturnFalse_WhenRecipeNotFound() {
        when(recipeClient.getRecipeById(1L)).thenThrow(new RuntimeException());
        boolean exists = reviewService.checkRecipeExists(1L);
        assertFalse(exists);
    }

    @Test
    void calculateAverageRating_ShouldReturnCorrectAverage() {
        Long recipeId = 1L;
        Review review1 = Review.builder().recipeId(recipeId).userId(2L).rating(5).comment("Great").build();
        Review review2 = Review.builder().recipeId(recipeId).userId(3L).rating(3).comment("Okay").build();
        when(reviewRepository.findByRecipeId(recipeId)).thenReturn(List.of(review1, review2));
        double avgRating = reviewService.calculateAverageRating(recipeId);
        assertEquals(4.0, avgRating);
    }

    @Test
    void calculateAverageRating_ShouldReturnZero_WhenNoReviews() {
        when(reviewRepository.findByRecipeId(1L)).thenReturn(List.of());
        double avgRating = reviewService.calculateAverageRating(1L);
        assertEquals(0.0, avgRating);
    }
}
