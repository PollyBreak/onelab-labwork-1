package com.polina.authservice.security;

import com.polina.dto.TokenValidationResponse;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private String token;
    private final Long userId = 123L;
    private final String username = "testUser";
    private final String role = "ROLE_USER";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        token = jwtUtil.generateToken(userId, username, role);
    }

    @Test
    void testGenerateToken() {
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testExtractUsername() {
        String extractedUsername = jwtUtil.extractUsername(token);
        assertEquals(username, extractedUsername);
    }

    @Test
    void testExtractClaims() {
        Claims claims = jwtUtil.extractClaims(token);
        assertNotNull(claims);
        assertEquals(username, claims.getSubject());
        assertEquals(userId, claims.get("userId", Long.class));
        assertEquals(role, claims.get("role", String.class));
    }

    @Test
    void testValidateToken_Success() {
        TokenValidationResponse response = jwtUtil.validateToken("Bearer " + token);
        assertTrue(response.isValid());
        assertEquals(userId, response.getUserId());
        assertEquals(username, response.getUsername());
        assertEquals(role, response.getRole());
    }

    @Test
    void testValidateToken_InvalidToken() {
        TokenValidationResponse response = jwtUtil.validateToken("Bearer invalid.token.here");
        assertFalse(response.isValid());
        assertNull(response.getUserId());
        assertNull(response.getUsername());
        assertNull(response.getRole());
    }

    @Test
    void testValidateToken_NullToken() {
        TokenValidationResponse response = jwtUtil.validateToken(null);
        assertFalse(response.isValid());
    }

    @Test
    void testValidateToken_NoBearerPrefix() {
        TokenValidationResponse response = jwtUtil.validateToken(token);
        assertFalse(response.isValid());
    }
}
