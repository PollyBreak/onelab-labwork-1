package com.polina.lab1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeDTO {
    private Long id;
    private String title;
    //many-to-one
    private Long authorId;
    private String instructions;
    private String description;

    @Builder.Default
    private List<Long> productIds = new ArrayList<>();
}
