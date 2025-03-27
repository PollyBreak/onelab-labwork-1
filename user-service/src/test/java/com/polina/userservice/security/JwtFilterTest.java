package com.polina.userservice.security;

import com.polina.dto.TokenValidationResponse;
import com.polina.userservice.client.AuthClient;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtFilterTest {
    @Mock
    private AuthClient authClient;
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
    void doFilterInternal_NoToken_ContinuesFilterChain() throws ServletException, IOException {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);
        jwtFilter.doFilterInternal(request, response, filterChain);
        verify(filterChain, times(1)).doFilter(request, response);
        verify(response, never()).sendError(anyInt(), anyString());
    }

    @Test
    void doFilterInternal_InvalidTokenFormat_ContinuesFilterChain() throws ServletException, IOException {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("InvalidTokenFormat");
        jwtFilter.doFilterInternal(request, response, filterChain);
        verify(filterChain, times(1)).doFilter(request, response);
        verify(response, never()).sendError(anyInt(), anyString());
    }

    @Test
    void doFilterInternal_ValidToken_SetsAuthentication() throws ServletException, IOException {
        String validToken = "Bearer validToken";
        TokenValidationResponse mockResponse = new TokenValidationResponse
                (true, 1L, "testUser", "ROLE_USER");
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(validToken);
        when(authClient.validateToken(validToken)).thenReturn(mockResponse);
        jwtFilter.doFilterInternal(request, response, filterChain);
        verify(authClient, times(1)).validateToken(validToken);
        verify(filterChain, times(1)).doFilter(request, response);
        verify(response, never()).sendError(anyInt(), anyString());
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(1L, SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal());
    }

    @Test
    void doFilterInternal_InvalidToken_RespondsForbidden() throws ServletException, IOException {
        String invalidToken = "Bearer invalidToken";
        TokenValidationResponse mockResponse = new TokenValidationResponse
                (false, null, null, null);
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(invalidToken);
        when(authClient.validateToken(invalidToken)).thenReturn(mockResponse);
        jwtFilter.doFilterInternal(request, response, filterChain);
        verify(authClient, times(1)).validateToken(invalidToken);
        verify(response, times(1))
                .sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or Expired Token");
        verify(filterChain, never()).doFilter(any(), any());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_ExceptionDuringValidation_RespondsForbidden()
            throws ServletException, IOException {
        String token = "Bearer validToken";
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(token);
        when(authClient.validateToken(token)).thenThrow
                (new RuntimeException("Auth service unavailable"));
        jwtFilter.doFilterInternal(request, response, filterChain);
        verify(authClient, times(1)).validateToken(token);
        verify(response, times(1))
                .sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or Expired Token");
        verify(filterChain, never()).doFilter(any(), any());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
