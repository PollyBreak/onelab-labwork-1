package com.polina.apigateway;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class ApiGatewayLoggingFilter {
    private static final Logger logger = LoggerFactory.getLogger(ApiGatewayLoggingFilter.class);

    @Bean
    public GlobalFilter globalFilter() {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            long startTime = System.currentTimeMillis();
            logger.info("Incoming request: {} {}", request.getMethod(), request.getURI());
            logger.info("Request Headers: {}", request.getHeaders());
            logger.info("Query Parameters: {}", request.getQueryParams());
            return chain.filter(exchange)
                    .doOnSuccess(aVoid -> {
                        long duration = System.currentTimeMillis() - startTime;

                        logger.info("Response Status: {}", response.getStatusCode());
                        logger.info("Response Headers: {}", response.getHeaders());
                        logger.info("Processing Time: {} ms", duration);
                    });
        };
    }
}