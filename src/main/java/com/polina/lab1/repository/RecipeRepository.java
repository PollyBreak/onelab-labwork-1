package com.polina.lab1.repository;

import com.polina.lab1.dto.RecipeDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<RecipeDTO, Long> {
    List<RecipeDTO> findByAuthorId(Long authorId);
}
