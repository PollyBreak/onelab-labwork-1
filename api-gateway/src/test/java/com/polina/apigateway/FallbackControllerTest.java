package com.polina.apigateway;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FallbackControllerTest {

    @InjectMocks
    private FallbackController fallbackController;

    @Test
    void testUserServiceFallback() {
        ResponseEntity<String> response = fallbackController.userServiceFallback();
        assertEquals("User service is temporarily unavailable. Please try again later.",
                response.getBody());
    }

    @Test
    void testRecipeServiceFallback() {
        ResponseEntity<String> response = fallbackController.recipeServiceFallback();
        assertEquals("Recipe service is temporarily unavailable. Please try again later.",
                response.getBody());
    }

    @Test
    void testAuthServiceFallback() {
        ResponseEntity<String> response = fallbackController.authServiceFallback();
        assertEquals("Auth service is temporarily unavailable. Please try again later.",
                response.getBody());
    }

    @Test
    void testReviewsServiceFallback() {
        ResponseEntity<String> response = fallbackController.reviewsServiceFallback();
        assertEquals("Reviews service is temporarily unavailable. Please try again later.",
                response.getBody());
    }
}
