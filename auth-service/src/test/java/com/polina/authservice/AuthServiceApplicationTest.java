package com.polina.authservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;


import static org.mockito.Mockito.*;

@SpringBootTest
class AuthServiceApplicationTest {

    @Test
    void mainMethodTest() {
        SpringApplication mockSpringApplication = mock(SpringApplication.class);

        AuthServiceApplication.main(new String[]{});

    }
}

