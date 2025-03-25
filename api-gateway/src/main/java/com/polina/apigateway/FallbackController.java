package com.polina.apigateway;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    @GetMapping("/fallback/user")
    public ResponseEntity<String> userServiceFallback() {
        return ResponseEntity.ok("User service is temporarily unavailable. Please try again later.");
    }

    @GetMapping("/fallback/recipe")
    public ResponseEntity<String> recipeServiceFallback() {
        return ResponseEntity.ok("Recipe service is temporarily unavailable. Please try again later.");
    }

    @GetMapping("/fallback/auth")
    public ResponseEntity<String> authServiceFallback() {
        return ResponseEntity.ok("Auth service is temporarily unavailable. Please try again later.");
    }

    @GetMapping("/fallback/reviews")
    public ResponseEntity<String> reviewsServiceFallback() {
        return ResponseEntity.ok("Reviews service is temporarily unavailable. Please try again later.");
    }
}