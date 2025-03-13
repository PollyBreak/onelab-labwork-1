package com.polina.userservice.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPreferencesDTO {
    private Long userId;
    private List<String> favoriteIngredients;
}
