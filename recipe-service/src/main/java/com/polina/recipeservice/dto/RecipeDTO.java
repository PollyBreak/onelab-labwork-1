package com.polina.recipeservice.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeDTO {
    private Long id;

    @NotBlank(message = "Title cannot be blank")
    private String title;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    @NotBlank(message = "Instructions cannot be blank")
    private String instructions;

    @NotNull(message = "Author ID is required")
    private Long authorId;

    private List<String> products;
    private double averageRating;

    public RecipeDTO(Long id, String title, String instructions, Long authorId) {
        this.id = id;
        this.title = title;
        this.instructions = instructions;
        this.authorId = authorId;
    }

}
