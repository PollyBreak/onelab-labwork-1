package com.polina.reviewservice.client;

import com.polina.dto.TokenValidationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "auth-service")
public interface AuthClient {

    @GetMapping("/auth/validate")
    TokenValidationResponse validateToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String token);
}
