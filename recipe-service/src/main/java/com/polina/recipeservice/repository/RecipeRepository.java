package com.polina.recipeservice.repository;

import com.polina.recipeservice.dto.RecipeDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<RecipeDTO, Long> {
    List<RecipeDTO> findByAuthorId(Long authorId);

    @Query("SELECT r FROM RecipeDTO r")
    List<RecipeDTO> getAllRecipes();

}
