package com.polina.apigateway;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class ApiGatewayLoggingFilter {
    private static final Logger logger = LoggerFactory.getLogger(ApiGatewayLoggingFilter.class);

    @Bean
    public GlobalFilter globalFilter() {
        return (exchange, chain) -> {
            logger.info("Incoming request: {}", exchange.getRequest().getURI());
            return chain.filter(exchange).then(Mono.fromRunnable(() ->
                    logger.info("Response sent: {}", exchange.getResponse().getStatusCode())));
        };
    }
}