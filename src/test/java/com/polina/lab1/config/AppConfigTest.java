package com.polina.lab1.config;

import com.polina.lab1.CommandLineApp;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = AppConfig.class)
class AppConfigTest {
    @Autowired
    private ApplicationContext applicationContext;
    @MockitoBean
    private CommandLineApp commandLineApp;

    @Test
    void givenAppConfig_whenContextLoads_thenBeansAreAvailable() {
        assertNotNull(applicationContext.getBean(Scanner.class),
                "Scanner bean should be available");
        assertNotNull(applicationContext.getBean(EntityManager.class),
                "EntityManager bean should be available");
    }

    @Test
    void givenAppConfig_whenGetScannerBean_thenReturnsSameInstance() {
        Scanner scanner1 = applicationContext.getBean(Scanner.class);
        Scanner scanner2 = applicationContext.getBean(Scanner.class);
        assertSame(scanner1, scanner2, "Scanner bean should be singleton");
    }
}
