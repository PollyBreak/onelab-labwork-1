package com.polina.lab1;

import com.polina.lab1.dto.RecipeDTO;
import com.polina.lab1.dto.UserDTO;
import com.polina.lab1.service.RecipeService;
import com.polina.lab1.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CommandLineAppTest {
    @Mock
    private UserService userService;
    @Mock
    private RecipeService recipeService;
    @InjectMocks
    private CommandLineApp commandLineApp;

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        commandLineApp = new CommandLineApp(userService,
                recipeService,
                new Scanner(new ByteArrayInputStream("6\n".getBytes())));
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    private CommandLineApp createCommandLineAppWithInput(String input) {
        return new CommandLineApp(userService, recipeService,
                new Scanner(new ByteArrayInputStream(input.getBytes())));
    }

    @Test
    void givenValidUserDetails_whenCreateUser_thenUserIsSuccessfullyCreated() {
        commandLineApp = createCommandLineAppWithInput("testuser\ntest@fff.com\n");
        commandLineApp.createUser();
        verify(userService, times(1)).saveUser(argThat(user ->
                user.getUsername().equals("testuser") && user.getEmail().equals("test@fff.com")
        ));
    }

    @Test
    void givenInvalidUserDetails_whenCreateUser_thenErrorMessageIsDisplayed() {
        commandLineApp = createCommandLineAppWithInput("testuser\ntest@fff.com\n");
        doThrow(new IllegalArgumentException("Invalid user")).when(userService).saveUser(any());
        commandLineApp.createUser();
        assertTrue(outputStreamCaptor.toString().contains("Invalid user"));
    }

    @Test
    void givenNoUsersExist_whenShowAllUsers_thenDisplaysNoUsersFoundMessage() {
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());
        commandLineApp.showAllUsers();
        assertTrue(outputStreamCaptor.toString().contains("No users found."));
    }

    @Test
    void givenUsersExist_whenShowAllUsers_thenDisplaysAllUsers() {
        when(userService.getAllUsers()).thenReturn(List.of(
                UserDTO.builder().id(1L).username("Alice").email("alice@mail.com").build()
        ));
        commandLineApp.showAllUsers();
        assertTrue(outputStreamCaptor.toString().contains("Alice"));
    }

    @Test
    void givenRecipesExist_whenShowAllRecipes_thenDisplaysAllRecipes() {
        RecipeDTO recipe = RecipeDTO.builder()
                .id(1L).title("Pasta").authorId(1L).description("tasty")
                .products(Collections.emptyList()).build();
        when(recipeService.getAllRecipes()).thenReturn(List.of(recipe));
        commandLineApp.showAllRecipes();
        assertTrue(outputStreamCaptor.toString().contains("Pasta"));
    }

    @Test
    void givenInvalidUserId_whenShowUserRecipes_thenDisplaysErrorMessage() {
        commandLineApp = createCommandLineAppWithInput("999\n");
        doThrow(new NoSuchElementException("User not found")).when(recipeService).getRecipesByUser(anyLong());
        commandLineApp.showUserRecipes();
        assertTrue(outputStreamCaptor.toString().contains("User not found"));
    }

    @Test
    void givenValidRecipeDetails_whenCreateRecipe_thenRecipeIsSuccessfullyCreated() {
        commandLineApp = createCommandLineAppWithInput("1\nPasta\nTasty\nBoil water\ntomato\ndone\n");
        when(userService.findUserById(anyLong())).thenReturn(UserDTO.builder().id(1L).build());
        commandLineApp.createRecipe();
        verify(recipeService, times(1)).addRecipe(anyLong(), any(), any());
        assertTrue(outputStreamCaptor.toString().contains("Recipe created successfully!"));
    }

    @Test
    void givenInvalidProductInput_whenCreateRecipe_thenHandlesProductEntryCorrectly() {
        commandLineApp = createCommandLineAppWithInput("1\nPasta\nTasty\nBoil water\ninvalid_product\ndone\n");
        when(userService.findUserById(anyLong())).thenReturn(UserDTO.builder().id(1L).build());
        commandLineApp.createRecipe();
        verify(recipeService, times(1)).addRecipe(anyLong(), any(), any());
    }

    @Test
    void givenInvalidInput_whenGetIntInput_thenHandlesRetryAndReturnsValidInput() {
        commandLineApp = createCommandLineAppWithInput("abc\n3\n");
        assertEquals(3, commandLineApp.getIntInput());
    }

    @Test
    void givenInvalidInput_whenGetLongInput_thenHandlesRetryAndReturnsValidInput() {
        commandLineApp = createCommandLineAppWithInput("xyz\n456\n");
        assertEquals(456L, commandLineApp.getLongInput());
    }

    @Test
    void givenMultipleMenuOptions_whenRun_thenExecutesCorrectCommands() {
        commandLineApp = createCommandLineAppWithInput("3\n4\n6\n");
        assertDoesNotThrow(() -> commandLineApp.run());
        verify(userService, times(1)).getAllUsers();
        verify(recipeService, times(1)).getAllRecipes();
    }

    @Test
    void givenInvalidAndValidMenuOptions_whenRun_thenHandlesInvalidInputAndExecutesValidCommand() {
        commandLineApp = createCommandLineAppWithInput("999\n3\n6\n");
        assertDoesNotThrow(() -> commandLineApp.run());
        verify(userService, times(1)).getAllUsers();
    }
}
