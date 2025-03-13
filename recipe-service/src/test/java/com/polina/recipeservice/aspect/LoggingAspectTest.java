package com.polina.recipeservice.aspect;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LoggingAspectTest {

    @InjectMocks
    private LoggingAspect loggingAspect;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private ServletRequestAttributes attributes;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
        RequestContextHolder.setRequestAttributes(attributes);
    }

    @Test
    void testLogRequestResponse_Success() throws Throwable {
        Signature signature = mock(Signature.class);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.toString()).thenReturn("testMethod");

        when(attributes.getRequest()).thenReturn(request);
        when(attributes.getResponse()).thenReturn(response);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(request.getRequestURI()).thenReturn("/test");
        when(joinPoint.getArgs()).thenReturn(new Object[]{"arg1", 42});
        when(joinPoint.proceed()).thenReturn("Success");
        when(response.getStatus()).thenReturn(200);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testUser");

        Object result = loggingAspect.logRequestResponse(joinPoint);

        assertEquals("Success", result);
        verify(joinPoint, times(1)).proceed();
    }

    @Test
    void testLogRequestResponse_ExceptionThrown() throws Throwable {
        Signature signature = mock(Signature.class);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.toString()).thenReturn("testMethod");

        when(attributes.getRequest()).thenReturn(request);
        when(attributes.getResponse()).thenReturn(response);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(request.getRequestURI()).thenReturn("/test");
        when(joinPoint.getArgs()).thenReturn(new Object[]{"arg1", 42});
        when(joinPoint.proceed()).thenThrow(new RuntimeException("Test Exception"));

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testUser");

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> loggingAspect.logRequestResponse(joinPoint));
        assertEquals("Test Exception", thrown.getMessage());
    }

    @Test
    void testLogRequestResponse_NullRequestContext() throws Throwable {
        RequestContextHolder.setRequestAttributes(null);

        Signature signature = mock(Signature.class);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.toString()).thenReturn("testMethod");

        when(joinPoint.getArgs()).thenReturn(new Object[]{"arg1", 42});
        when(joinPoint.proceed()).thenReturn("Success");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn("AnonymousUser");

        Object result = loggingAspect.logRequestResponse(joinPoint);
        assertEquals("Success", result);
    }
}
