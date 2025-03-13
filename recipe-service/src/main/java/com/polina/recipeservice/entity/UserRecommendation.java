package com.polina.recipeservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "user_recommendations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRecommendation {
    @Id
    private Long userId;

    @ManyToMany
    @JoinTable(
            name = "user_recommended_recipes",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "recipe_id")
    )
    private List<Recipe> recommendedRecipes;
}