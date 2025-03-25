package com.polina.reviewservice.controller;

import com.polina.reviewservice.entity.Review;
import com.polina.reviewservice.service.ReviewService;
import jakarta.validation.Valid;
import com.polina.dto.ReviewDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/recipe/{recipeId}")
    @PreAuthorize("#reviewDTO.userId == authentication.principal")
    public ResponseEntity<String> addOrUpdateReview(@PathVariable Long recipeId,
                                                    @Valid @RequestBody ReviewDTO reviewDTO) {
        reviewService.addReview(recipeId, reviewDTO);
        return ResponseEntity.ok("Review added/updated successfully.");
    }

    @GetMapping("/recipe/{recipeId}")
    public ResponseEntity<List<Review>> getReviewsByRecipe(@PathVariable Long recipeId) {
        List<Review> reviews = reviewService.getReviewsForRecipe(recipeId);
        if (reviews.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(reviews);
    }
}
