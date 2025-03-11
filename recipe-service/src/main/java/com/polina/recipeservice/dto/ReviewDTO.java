package com.polina.recipeservice.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "reviews")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Recipe ID is required")
    private Long recipeId;

    @NotNull(message = "User ID is required")
    private Long userId;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private int rating;

    @Size(max = 255, message = "Comment cannot exceed 255 characters")
    private String comment;

    public ReviewDTO(long l, long l1, int i, String s) {
    }
}
