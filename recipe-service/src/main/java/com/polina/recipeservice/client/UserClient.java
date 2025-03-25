package com.polina.recipeservice.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/users/{id}")
    ResponseEntity<String> checkUserExists(@PathVariable Long id);

    @GetMapping("/users/preferences/{userId}")
    ResponseEntity<Map<String, Object>> getUserPreferences(@PathVariable Long userId);
}
