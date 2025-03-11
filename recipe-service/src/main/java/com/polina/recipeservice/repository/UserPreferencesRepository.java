package com.polina.recipeservice.repository;


import com.polina.recipeservice.dto.UserPreferencesDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPreferencesRepository extends JpaRepository<UserPreferencesDTO, Long> {
}
