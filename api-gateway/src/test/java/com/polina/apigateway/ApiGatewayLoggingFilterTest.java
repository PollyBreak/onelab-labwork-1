package com.polina.apigateway;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ApiGatewayLoggingFilterTest {

    @InjectMocks
    private ApiGatewayLoggingFilter apiGatewayLoggingFilter;

    @Mock
    private ServerWebExchange exchange;

    @Mock
    private ServerHttpRequest request;

    @Mock
    private ServerHttpResponse response;

    @BeforeEach
    void setUp() {
        when(exchange.getRequest()).thenReturn(request);
        when(exchange.getResponse()).thenReturn(response);
    }

    @Test
    void testGlobalFilter() {
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        when(request.getURI()).thenReturn(java.net.URI.create("http://localhost/test"));
        when(request.getHeaders()).thenReturn(new HttpHeaders());
        when(request.getQueryParams()).thenReturn(new org.springframework.util.LinkedMultiValueMap<>());
        when(response.getStatusCode()).thenReturn(HttpStatus.OK);
        when(response.getHeaders()).thenReturn(new HttpHeaders());

        GlobalFilter filter = apiGatewayLoggingFilter.globalFilter();
        assertNotNull(filter);

        Mono<Void> result = filter.filter(exchange, exchange -> Mono.empty());
        assertDoesNotThrow(() -> result.block());
    }
}
