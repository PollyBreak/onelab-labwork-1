package com.polina.userservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.polina.userservice.dto.UserDTO;
import com.polina.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class UserConsumer {
    @Autowired
    private UserService userService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "user.create", groupId = "user-group")
    public void consumeUserCreation(String userJson) {
        try {
            UserDTO user = objectMapper.readValue(userJson, UserDTO.class);
            userService.saveUser(user);
            kafkaTemplate.send("user.success.response", "User successfully created: " + user.getUsername());
        } catch (Exception e) {
            kafkaTemplate.send("user.error.response", "Error processing user creation: " + e.getMessage());
        }
    }

    @KafkaListener(topics = "user.all.request", groupId = "user-group")
    public void handleUserListRequest(String request) {
        try {
            List<UserDTO> users = userService.getAllUsers();

            if (users.isEmpty()) {
                kafkaTemplate.send("user.all.response", objectMapper.writeValueAsString(List.of("No users found.")));
            } else {
                kafkaTemplate.send("user.all.response", objectMapper.writeValueAsString(users));
            }
        } catch (Exception e) {
            kafkaTemplate.send("user.error.response", "Error fetching all users: " + e.getMessage());
        }
    }


}

