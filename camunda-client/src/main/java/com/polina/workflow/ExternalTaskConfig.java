package com.polina.workflow;

import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Configuration
public class ExternalTaskConfig {

    @Bean
    @ExternalTaskSubscription(
            topicName = "update-preferences",
            processDefinitionKey = "camunda-process",
            includeExtensionProperties = true,
            variableNames = "defaultScore"
    )
    public ExternalTaskHandler externalTaskHandler() {
        return (externalTask, externalTaskService) -> {
            System.out.println("Doing some business logic ");

            RestTemplate restTemplate = new RestTemplate();
            String url = "http://localhost:8081/users/preferences/11";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJwb2xpbmEiLCJ1c2VySWQiOjExLCJyb2xlIjoiVVNFUiIsImlhdCI6MTc0MjkwOTUzNCwiZXhwIjoxNzQyOTk1OTM0fQ.1NemViOFaHKfKM5evZNQ-LVajxOj324LIbc_ZDmeZd4");

            HttpEntity<String> requestEntity = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                List<String> ingredients = (List<String>) response.getBody().get("ingredients");
                System.out.println("Current favorite ingredients: " + ingredients);


                List<String> newIngredients = new ArrayList<>();
                newIngredients.add("parmesan");

                Map<String, Object> updateBody = new HashMap<>();
                updateBody.put("ingredients", newIngredients);

                HttpEntity<Map<String, Object>> updateRequest = new HttpEntity<>(updateBody, headers);
                ResponseEntity<String> updateResponse = restTemplate.exchange(url, HttpMethod.POST, updateRequest, String.class);

                ResponseEntity<Map> updatedResponse = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Map.class);
                if (updateResponse.getStatusCode() == HttpStatus.OK) {
                    List<String> updatedIngredients = (List<String>) updatedResponse.getBody().get("ingredients");
                    System.out.println("Updated favorite ingredients: " + updatedIngredients);
                    System.out.println("Successfully updated user preferences!");
                } else {
                    System.err.println("Failed to update user preferences: " + updateResponse.getStatusCode());
                }

                externalTaskService.complete(externalTask, Collections.singletonMap("favoriteIngredients", ingredients));
            } else {
                System.err.println("Failed to fetch user preferences: " + response.getStatusCode());
                externalTaskService.handleFailure(externalTask, "User preferences fetch failed", "Unable to retrieve preferences", 0, 1000);
            }
        };
    }
}