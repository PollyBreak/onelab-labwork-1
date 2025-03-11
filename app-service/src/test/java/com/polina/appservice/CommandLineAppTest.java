package com.polina.appservice;

import com.polina.appservice.kafka.ConsoleProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommandLineAppTest {
    @Mock
    private ConsoleProducer consoleProducer;
    @InjectMocks
    private CommandLineApp commandLineApp;

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        commandLineApp = new CommandLineApp();
        commandLineApp.consoleProducer = consoleProducer;
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    private CommandLineApp createCommandLineAppWithInput(String input) {
        CommandLineApp app = new CommandLineApp();
        app.consoleProducer = consoleProducer;
        app.scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        return app;
    }

    @Test
    void testCreateUser() {
        commandLineApp = createCommandLineAppWithInput("testuser\ntest@fff.com\n");
        commandLineApp.createUser();
        verify(consoleProducer, times(1)).sendUserCreation("testuser", "test@fff.com");
    }

    @Test
    void testCreateRecipe() {
        commandLineApp = createCommandLineAppWithInput("1\nPasta\nTasty\nBoil water\ningredient1,ingredient2\n");
        commandLineApp.createRecipe();
        verify(consoleProducer, times(1)).sendRecipeCreation(any());
    }

    @Test
    void testRateRecipe() {
        commandLineApp = createCommandLineAppWithInput("1\n2\n5\nGreat recipe!\n");
        commandLineApp.rateRecipe();
        verify(consoleProducer, times(1)).sendRecipeReview(2L, 1L, 5, "Great recipe!");
    }

    @Test
    void testSetFavoriteIngredients() {
        commandLineApp = createCommandLineAppWithInput("1\ntomato,cheese\n");
        commandLineApp.setFavoriteIngredients();
        verify(consoleProducer, times(1)).sendUserPreferences(1L, List.of("tomato", "cheese"));
    }

    @Test
    void testRequestRecommendedRecipes() {
        commandLineApp = createCommandLineAppWithInput("1\n");
        commandLineApp.requestRecommendedRecipes();
        verify(consoleProducer, times(1)).requestRecommendedRecipes(1L);
    }

    @Test
    void testRequestAllUsers() {
        commandLineApp.requestAllUsers();
        verify(consoleProducer, times(1)).requestAllUsers();
    }

    @Test
    void testRequestAllRecipes() {
        commandLineApp.requestAllRecipes();
        verify(consoleProducer, times(1)).requestAllRecipes();
    }

    @Test
    void testRequestRecipesByUser() {
        commandLineApp = createCommandLineAppWithInput("1\n");
        commandLineApp.requestRecipesByUser();
        verify(consoleProducer, times(1)).requestRecipesByUser(1L);
    }

    @Test
    void testGetIntInput_withInvalidThenValidInput() {
        commandLineApp = createCommandLineAppWithInput("abc\n3\n");
        assertEquals(3, commandLineApp.getIntInput());
    }

    @Test
    void testGetLongInput_withInvalidThenValidInput() {
        commandLineApp = createCommandLineAppWithInput("xyz\n456\n");
        assertEquals(456L, commandLineApp.getLongInput());
    }

    @Test
    void testWaitForDisplay() {
        assertDoesNotThrow(() -> commandLineApp.waitForDisplay());
    }

    @Test
    void testRun_withValidAndInvalidInputs() {
        commandLineApp = createCommandLineAppWithInput("invalid\n3\n9\n");
        assertDoesNotThrow(() -> commandLineApp.run());
    }
}
