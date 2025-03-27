package com.polina.apigateway;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class ApiGatewayApplicationTests {

    @Test
    void contextLoads() {
        assertDoesNotThrow(() -> new ApiGatewayApplication().main(new String[]{}));
    }
}
