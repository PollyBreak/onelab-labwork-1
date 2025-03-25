package com.polina.reviewservice.client;

import com.polina.dto.RecipeDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "recipe-service")
public interface RecipeClient{

    @GetMapping("/recipes/{recipeId}")
    ResponseEntity<RecipeDTO> getRecipeById(@PathVariable Long recipeId);

}
