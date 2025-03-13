package com.polina.recipeservice.repository;


import com.polina.recipeservice.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    List<Recipe> findByAuthorId(Long authorId);
}
