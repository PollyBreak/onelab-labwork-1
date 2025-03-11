package com.polina.recipeservice.repository;

import com.polina.recipeservice.dto.ReviewDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewDTO, Long> {
    List<ReviewDTO> findByRecipeId(Long recipeId);
}