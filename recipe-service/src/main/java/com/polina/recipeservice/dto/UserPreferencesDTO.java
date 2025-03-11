package com.polina.recipeservice.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "user_preferences")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferencesDTO {
    @Id
    private Long userId;

    @ElementCollection
    @NotNull(message = "Favorite ingredients list cannot be null")
    private List<String> favoriteIngredients;
}
