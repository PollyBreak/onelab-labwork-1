package com.polina.appservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class AppServiceApplicationTests {
    @MockitoBean
    private CommandLineApp commandLineApp;

    @Test
    void contextLoads() {
    }

}
