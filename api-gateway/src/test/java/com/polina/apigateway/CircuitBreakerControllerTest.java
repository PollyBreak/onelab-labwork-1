package com.polina.apigateway;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CircuitBreakerControllerTest {

    @InjectMocks
    private CircuitBreakerController circuitBreakerController;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        circuitBreakerController = new CircuitBreakerController(restTemplate);
    }

    @Test
    void testGetUsersSuccess() {
        String userId = "123";
        String expectedResponse = "User data";
        ResponseEntity<String> mockResponse = ResponseEntity.ok(expectedResponse);
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(mockResponse);
        ResponseEntity<String> response = circuitBreakerController.getUsers(userId);

        assertEquals(expectedResponse, response.getBody());
        verify(restTemplate, times(1)).
                getForEntity("http://localhost:8081/users/123", String.class);
    }

    @Test
    void testFallbackForUserService() {
        String userId = "123";
        Throwable throwable = new RuntimeException("Service failure");
        ResponseEntity<String> response = circuitBreakerController
                .fallbackForUserService(userId, throwable);
        assertEquals("User service is temporarily unavailable. Please try again later.",
                response.getBody());
    }
}
