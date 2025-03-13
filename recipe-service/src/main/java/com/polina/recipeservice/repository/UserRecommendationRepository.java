package com.polina.recipeservice.repository;

import com.polina.recipeservice.entity.UserRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRecommendationRepository extends JpaRepository<UserRecommendation, Long> {
}
