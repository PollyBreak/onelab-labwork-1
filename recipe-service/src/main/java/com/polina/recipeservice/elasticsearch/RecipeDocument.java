package com.polina.recipeservice.elasticsearch;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "recipes")
public class RecipeDocument {
    @Id
    private String id;
    private String title;
    private String description;
    private String cuisine;
    private List<String> products;
    private double averageRating;
}