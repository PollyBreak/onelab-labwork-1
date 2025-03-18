package com.polina.recipeservice.service;

import com.polina.recipeservice.entity.Review;

import java.util.List;

@FunctionalInterface
public interface RatingCalculator {
    double calculate(List<Review> reviews);
}
