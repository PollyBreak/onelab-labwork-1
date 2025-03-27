package com.polina.authservice.security;


import com.polina.dto.TokenValidationResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.mockito.Mockito.*;

class JwtFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtFilter jwtFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();
    }

    @Test
    void testDoFilterInternal_ValidToken() throws ServletException, IOException {
        String token = "Bearer valid.token.here";
        TokenValidationResponse validationResponse = new TokenValidationResponse(true, 1L, "testuser", "ROLE_USER");

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(token);
        when(jwtUtil.validateToken(token)).thenReturn(validationResponse);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_InvalidToken() throws ServletException, IOException {
        String token = "Bearer invalid.token.here";
        TokenValidationResponse validationResponse = new TokenValidationResponse(false, null, null, null);

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(token);
        when(jwtUtil.validateToken(token)).thenReturn(validationResponse);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(response, times(1)).sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or Expired Token");
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_NoToken() throws ServletException, IOException {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtUtil, never()).validateToken(any());
    }

    @Test
    void testDoFilterInternal_ExceptionHandling() throws ServletException, IOException {
        String token = "Bearer token.here";

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(token);
        when(jwtUtil.validateToken(token)).thenThrow(new RuntimeException("Unexpected error"));

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(response, times(1)).sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or Expired Token");
        verify(filterChain, never()).doFilter(request, response);
    }
}
