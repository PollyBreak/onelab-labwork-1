package com.polina.authservice.security;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    private String validToken;
    private String expiredToken;
    private String malformedToken = "verybadtokenmalfored";
    private String invalidSignatureToken;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        validToken = jwtUtil.generateToken("testUser");
        invalidSignatureToken = validToken.substring(0, validToken.length() - 5) + "abcde"; // Break signature
    }

    @Test
    void testGetSigningKey() {
        assertNotNull(jwtUtil.generateToken("testUser"));
    }

    @Test
    void testGenerateToken() {
        assertNotNull(validToken);
        assertFalse(validToken.isEmpty());
    }

    @Test
    void testValidateToken_ValidToken() {
        assertTrue(jwtUtil.validateToken(validToken));
    }

    @Test
    void testValidateToken_ExpiredToken() {
        expiredToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0VXNlciIsImlhdCI6MTYwMDAwMDAwMCwiZXhwIjoxNjAwMDAwMDAwfQ.signature";
        assertFalse(jwtUtil.validateToken(expiredToken));
    }

    @Test
    void testValidateToken_MalformedToken() {
        assertFalse(jwtUtil.validateToken(malformedToken));
    }

    @Test
    void testValidateToken_InvalidSignature() {
        assertFalse(jwtUtil.validateToken(invalidSignatureToken));
    }

    @Test
    void testValidateToken_UnsupportedJwt() {
        String unsupportedToken = "eyJhbGciOiJub25lIn0.eyJzdWIiOiJ1bnN1cHBvcnRlZCJ9.";
        assertFalse(jwtUtil.validateToken(unsupportedToken));
    }

    @Test
    void testValidateToken_GeneralException() {
        assertFalse(jwtUtil.validateToken(null));
    }

    @Test
    void testExtractUsername_ValidToken() {
        String username = jwtUtil.extractUsername(validToken);
        assertEquals("testUser", username);
    }
}
