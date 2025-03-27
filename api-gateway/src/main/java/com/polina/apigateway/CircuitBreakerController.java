package com.polina.apigateway;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class CircuitBreakerController {

    private final RestTemplate restTemplate;

    @Autowired
    public CircuitBreakerController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private static final String USER_SERVICE = "userService";

    @GetMapping("/api/users")
    @CircuitBreaker(name = USER_SERVICE, fallbackMethod = "fallbackForUserService")
    public ResponseEntity<String> getUsers(@RequestParam String id) {
        String url = "http://localhost:8081/users/" + id;
        return restTemplate.getForEntity(url, String.class);
    }

    public ResponseEntity<String> fallbackForUserService(String id, Throwable t) {
        return ResponseEntity.ok("User service is temporarily unavailable. Please try again later.");
    }
}


