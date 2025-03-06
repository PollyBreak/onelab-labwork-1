package com.polina.lab1;

import com.polina.lab1.dto.ProductDTO;
import com.polina.lab1.dto.RecipeDTO;
import com.polina.lab1.dto.UserDTO;
import com.polina.lab1.service.RecipeService;
import com.polina.lab1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

@Component
public class CommandLineApp implements CommandLineRunner {
    private final UserService userService;
    private final RecipeService recipeService;
    private final Scanner scanner;

    @Autowired
    public CommandLineApp(UserService userService,
                          RecipeService recipeService,
                          Scanner scanner){
        this.userService = userService;
        this.recipeService = recipeService;
        this.scanner = scanner;
    }

    @Override
    public void run(String... args) {
        while (true) {
            System.out.println("------------Recipe App Menu------------");
            System.out.println("Choose ");
            System.out.println("1 - to create user");
            System.out.println("2 - to create recipe");
            System.out.println("3 - to show all users");
            System.out.println("4 - to show all recipes");
            System.out.println("5 - to show user's recipes");
            System.out.println("6 - exit");
            System.out.print("Enter your option: ");
            int option = getIntInput();

            switch (option) {
                case 1 -> createUser();
                case 2 -> createRecipe();
                case 3 -> showAllUsers();
                case 4 -> showAllRecipes();
                case 5 -> showUserRecipes();
                case 6 -> {
                    System.out.println("Finishing application.");
                    return;
                }
                default -> System.out.println("You entered the wrong number! Please try again.");
            }
        }
    }

    public void createUser() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        try {
            userService.saveUser(UserDTO.builder().username(username).email(email).build());
            System.out.println("User created successfully!");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage() + " Please, try again. User was not registered.");
        }
    }

    public void showAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("No users found.");
        } else {
            users.forEach(user -> System.out.printf("ID: %d | Username: %s | Email: %s%n",
                    user.getId(), user.getUsername(), user.getEmail()));
        }
    }

    public void showAllRecipes() {
        List<RecipeDTO> recipes = recipeService.getAllRecipes();
        printRecipes(recipes);
    }

    public void printRecipes(List<RecipeDTO> recipes){
        recipes.forEach(recipe -> {
            System.out.println("\nRecipe:");
            System.out.println("id: " + recipe.getId());
            System.out.println("author id: " + recipe.getAuthorId());
            System.out.println("title: " + recipe.getTitle());
            System.out.println("description: " + recipe.getDescription());
            System.out.println("products:");
            recipe.getProducts()
                    .forEach(product -> System.out.println(" - " + product.getName()));
            System.out.println();
        });
    }

    public void showUserRecipes() {
        System.out.print("Enter user id: ");
        Long userId = getLongInput();
        try {
            List<RecipeDTO> recipes = recipeService.getRecipesByUser(userId);
            if (recipes.isEmpty()) {
                System.out.println("No recipes was found for this user.");
            } else {
                printRecipes(recipes);
            }
        } catch (NoSuchElementException e) {
            System.out.println(e.getMessage());
        }
    }


    public void createRecipe() {
        System.out.print("Enter user id: ");
        Long userId = getLongInput();
        try {
            userService.findUserById(userId);
        } catch (NoSuchElementException e) {
            System.out.println(e.getMessage());
            return;
        }
        System.out.print("Enter recipe title: ");
        String title = scanner.nextLine();
        System.out.print("Enter recipe description: ");
        String description = scanner.nextLine();
        System.out.print("Enter instructions: ");
        String instructions = scanner.nextLine();
        List<ProductDTO> products = new ArrayList<>();
        while (true) {
            System.out.print("Enter product name (or type 'done' to finish): ");
            String productName = scanner.nextLine();
            if (productName.equalsIgnoreCase("done")) break;
            products.add(ProductDTO.builder()
                    .name(productName)
                    .build());
        }
        RecipeDTO recipe = RecipeDTO.builder()
                .title(title)
                .description(description)
                .instructions(instructions)
                .build();
        recipeService.addRecipe(userId, recipe, products);
        System.out.println("Recipe created successfully!");
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


}