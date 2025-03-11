package com.polina.appservice;

import com.polina.appservice.kafka.ConsoleProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@Component
public class CommandLineApp implements CommandLineRunner {
    @Autowired
    ConsoleProducer consoleProducer;

    public Scanner scanner = new Scanner(System.in);

    @Override
    public void run(String... args) {
        System.out.print("Enter your user ID: ");

        while (true) {
            System.out.println("\n------------ Recipe App Menu ------------");
            System.out.println("1 - Create User");
            System.out.println("2 - Create Recipe");
            System.out.println("3 - Show All Users");
            System.out.println("4 - Show All Recipes");
            System.out.println("5 - Show User Recipes");
            System.out.println("6 - Rate a Recipe");
            System.out.println("7 - Set Favorite Ingredients");
            System.out.println("8 - Get Recommended Recipes");
            System.out.println("9 - Exit");
            System.out.print("Enter your option: ");

            int option = getIntInput();
            switch (option) {
                case 1 -> createUser();
                case 2 -> createRecipe();
                case 3 -> requestAllUsers();
                case 4 -> requestAllRecipes();
                case 5 -> requestRecipesByUser();
                case 6 -> rateRecipe();
                case 7 -> setFavoriteIngredients();
                case 8 -> requestRecommendedRecipes();
                case 9 -> {
                    System.out.println("Exiting application. Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid option! Please try again.");
            }
        }
    }

    public void rateRecipe() {
        System.out.print("Enter user ID: ");
        Long userId = getLongInput();
        System.out.print("Enter recipe ID: ");
        Long recipeId = getLongInput();
        System.out.print("Enter your rating (1-5): ");
        int rating = getIntInput();
        System.out.print("Enter a comment: ");
        String comment = scanner.nextLine();
        consoleProducer.sendRecipeReview(recipeId, userId, rating, comment);
    }

    public void setFavoriteIngredients() {
        System.out.print("Enter user ID: ");
        Long userId = getLongInput();
        System.out.print("Enter your favorite ingredients (comma-separated): ");
        List<String> ingredients = List.of(scanner.nextLine().split(","));
        consoleProducer.sendUserPreferences(userId, ingredients);
        System.out.println("Your favorite ingredients were saved.");
    }

    public void requestRecommendedRecipes() {
        System.out.print("Enter user ID: ");
        Long userId = getLongInput();
        consoleProducer.requestRecommendedRecipes(userId);
        System.out.println("Fetching recommended recipes...");
        waitForDisplay();
    }

    public void createUser() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        consoleProducer.sendUserCreation(username, email);
        System.out.println("User creation request sent.");
    }

    public void createRecipe() {
        System.out.print("Enter user ID (author ID): ");
        Long authorId = getLongInput();
        System.out.print("Enter recipe title: ");
        String title = scanner.nextLine();
        System.out.print("Enter description: ");
        String description = scanner.nextLine();
        System.out.print("Enter instructions: ");
        String instructions = scanner.nextLine();

        System.out.print("Enter product names (comma-separated): ");
        List<String> products = List.of(scanner.nextLine().split(","));

        Map<String, Object> recipeMap = new HashMap<>();
        recipeMap.put("title", title);
        recipeMap.put("description", description);
        recipeMap.put("instructions", instructions);
        recipeMap.put("authorId", authorId);
        recipeMap.put("products", products);

        consoleProducer.sendRecipeCreation(recipeMap);
        System.out.println("Recipe creation request sent.");
    }


    public void requestAllUsers() {
        consoleProducer.requestAllUsers();
        System.out.println("Fetching all users...");
        waitForDisplay();
    }

    public void requestAllRecipes() {
        consoleProducer.requestAllRecipes();
        System.out.println("Fetching all recipes...");
        waitForDisplay();
    }

    public void requestRecipesByUser() {
        System.out.print("Enter user ID: ");
        Long userId = getLongInput();
        consoleProducer.requestRecipesByUser(userId);
        System.out.println("Fetching recipes for user ID: " + userId);
        waitForDisplay();
    }

    public int getIntInput() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a valid number: ");
            }
        }
    }

    public Long getLongInput() {
        while (true) {
            try {
                return Long.parseLong(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a valid ID: ");
            }
        }
    }

    public void waitForDisplay() {
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}