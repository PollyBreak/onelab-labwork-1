package com.polina.authservice.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenValidationResponse {
    private boolean valid;
    private Long userId;
    private String username;
    private String role;
}
