package com.polina.recipeservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.http.ResponseEntity;

@FeignClient(name = "auth-service")
public interface AuthClient {
    @GetMapping("/auth/validate")
    ResponseEntity<Long> validateToken(@RequestHeader("Authorization") String token);
}
