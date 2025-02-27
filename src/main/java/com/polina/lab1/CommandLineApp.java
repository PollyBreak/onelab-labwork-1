package com.polina.lab1;

import com.polina.lab1.dto.ProductDTO;
import com.polina.lab1.dto.RecipeDTO;
import com.polina.lab1.dto.UserDTO;
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
    private final Scanner scanner = new Scanner(System.in);

    @Autowired
    public CommandLineApp(UserService userService){
        this.userService = userService;
    }

    @Override
    public void run(String... args) {
        while (true) {
            System.out.println("------------Recipe App Menu------------");
            System.out.println("Choose ");
            System.out.println("1 - to create user");
            System.out.println("2 - to create recipe");
            System.out.println("3 - to show all users");
            System.out.println("4 - to show user's recipes");
            System.out.println("5 - exit");
            System.out.print("Enter your option: ");
            int option = getIntInput();

            switch (option) {
                case 1 -> createUser();
                case 2 -> createRecipe();
                case 3 -> showAllUsers();
                case 4 -> showUserRecipes();
                case 5 -> {
                    System.out.println("Finishing application.");
                    return;
                }
                default -> System.out.println("You entered the wrong number! Please try again.");
            }
        }
    }

    private void createUser() {
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

    private void showAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("No users found.");
        } else {
            users.forEach(user -> System.out.printf("ID: %d | Username: %s | Email: %s%n",
                    user.getId(), user.getUsername(), user.getEmail()));
        }
    }

    private void showUserRecipes() {
        System.out.print("Enter user id: ");
        Long userId = getLongInput();
        try {
            List<RecipeDTO> recipes = userService.getRecipesByUser(userId);
            if (recipes.isEmpty()) {
                System.out.println("No recipes was found for this user.");
            } else {
                recipes.forEach(recipe -> {
                    System.out.println("\nRecipe:");
                    System.out.println("id: " + recipe.getId());
                    System.out.println("title: " + recipe.getTitle());
                    System.out.println("description: " + recipe.getDescription());
                    System.out.println("products:");
                    recipe.getProductIds().forEach(productId -> {
                        ProductDTO product = userService.getProductById(productId);
                        if (product != null) {
                            System.out.println(" - " + product.getName());
                        }
                    });
                    System.out.println();
                });
            }
        } catch (NoSuchElementException e) {
            System.out.println(e.getMessage());
        }
    }


    private void createRecipe() {
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
        userService.addRecipe(userId, recipe, products);
        System.out.println("Recipe created successfully!");
    }

    private int getIntInput() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a valid number: ");
            }
        }
    }

    private Long getLongInput() {
        while (true) {
            try {
                return Long.parseLong(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a valid ID: ");
            }
        }
    }
}