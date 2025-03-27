package com.polina.reviewservice.controller;

import com.polina.reviewservice.entity.Review;
import com.polina.reviewservice.service.ReviewService;
import com.polina.dto.ReviewDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReviewControllerTest {
    private ReviewService reviewService;
    private ReviewController reviewController;
    private Authentication authentication;
    private SecurityContext securityContext;

    @BeforeEach
    void setUp() {
        reviewService = mock(ReviewService.class);
        reviewController = new ReviewController(reviewService);
        authentication = mock(Authentication.class);
        securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void addOrUpdateReview_ShouldCallServiceAndReturnSuccessResponse() {
        Long recipeId = 1L;
        ReviewDTO reviewDTO = ReviewDTO.builder()
                .userId(2L)
                .rating(5)
                .comment("Amazing!")
                .build();
        when(authentication.getPrincipal()).thenReturn(2L);
        ResponseEntity<String> response = reviewController.addOrUpdateReview(recipeId, reviewDTO);
        verify(reviewService).addReview(recipeId, reviewDTO);
        assertEquals("Review added/updated successfully.", response.getBody());
        assertEquals(200, response.getStatusCodeValue());
    }


    @Test
    void getReviewsByRecipe_ShouldReturnReviews_WhenPresent() {
        Long recipeId = 1L;
        Review review1 = Review.builder().recipeId(recipeId).userId(2L).rating(5).comment("Excellent").build();
        Review review2 = Review.builder().recipeId(recipeId).userId(3L).rating(4).comment("Good").build();
        when(reviewService.getReviewsForRecipe(recipeId)).thenReturn(List.of(review1, review2));
        ResponseEntity<List<Review>> response = reviewController.getReviewsByRecipe(recipeId);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        assertEquals("Excellent", response.getBody().get(0).getComment());
        assertEquals("Good", response.getBody().get(1).getComment());
    }

    @Test
    void getReviewsByRecipe_ShouldReturnNoContent_WhenNoReviews() {
        Long recipeId = 1L;
        when(reviewService.getReviewsForRecipe(recipeId)).thenReturn(List.of());
        ResponseEntity<List<Review>> response = reviewController.getReviewsByRecipe(recipeId);
        assertEquals(204, response.getStatusCodeValue());
        assertNull(response.getBody());
    }
}
