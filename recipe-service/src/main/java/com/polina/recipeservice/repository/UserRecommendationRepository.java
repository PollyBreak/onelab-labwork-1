package com.polina.recipeservice.repository;

import com.polina.recipeservice.entity.UserRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface UserRecommendationRepository extends JpaRepository<UserRecommendation, Long> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM user_recommended_recipes WHERE recipe_id = :recipeId", nativeQuery = true)
    void deleteByRecipeId(@Param("recipeId") Long recipeId);
}
