package com.polina.recipeservice.repository;


import com.polina.recipeservice.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByRecipeId(Long recipeId);

    Optional<Review> findByRecipeIdAndUserId(Long recipeId, Long userId);
}
