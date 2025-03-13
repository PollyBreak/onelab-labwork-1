package com.polina.recipeservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.mock;

@SpringBootTest
class RecipeServiceApplicationTests {

    @Test
    void mainMethodTest() {
        SpringApplication mockSpringApplication = mock(SpringApplication.class);

        RecipeServiceApplication.main(new String[]{});
    }

}
