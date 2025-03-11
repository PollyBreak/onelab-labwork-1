package com.polina.recipeservice.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


@Entity
@Table(name="products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Product name cannot be blank")
    @Size(min = 2, max = 30, message = "Product name must be between 2 and 30 characters")
    @Column(unique = true, nullable = false)
    private String name;
}
