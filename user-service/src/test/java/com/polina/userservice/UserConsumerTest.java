package com.polina.userservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.polina.userservice.dto.UserDTO;
import com.polina.userservice.kafka.UserConsumer;
import com.polina.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserConsumerTest {

    @InjectMocks
    private UserConsumer userConsumer;

    @Mock
    private UserService userService;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    private UserDTO testUser;
    private String validUserJson;
    private String invalidUserJson;

    @BeforeEach
    void setUp() throws Exception {
        testUser = new UserDTO();
        testUser.setUsername("testUser");
        testUser.setEmail("test@example.com");

        validUserJson = "{\"username\": \"testUser\", \"email\": \"test@example.com\"}";
        invalidUserJson = "INVALID_JSON";

        lenient().when(objectMapper.readValue(validUserJson, UserDTO.class)).thenReturn(testUser);
        lenient().doThrow(new JsonProcessingException("Invalid JSON") {})
                .when(objectMapper).readValue(eq(invalidUserJson), eq(UserDTO.class));
    }


    @Test
    void consumeUserCreation_Success() {
        userConsumer.consumeUserCreation(validUserJson);

        verify(userService).saveUser(testUser);
        verify(kafkaTemplate).send("user.success.response", "User successfully created: testUser");
    }

    @Test
    void consumeUserCreation_Failure_InvalidJson() {
        userConsumer.consumeUserCreation(invalidUserJson);

        verify(kafkaTemplate).send(eq("user.error.response"), contains("Error processing user creation:"));
    }

    @Test
    void consumeUserCreation_Failure_UserServiceException() {
        doThrow(new RuntimeException("Database error")).when(userService).saveUser(any());

        userConsumer.consumeUserCreation(validUserJson);

        verify(kafkaTemplate).send(eq("user.error.response"), contains("Error processing user creation: Database error"));
    }





    @Test
    void handleUserListRequest_Failure_UserServiceException() {
        when(userService.getAllUsers()).thenThrow(new RuntimeException("Database error"));

        userConsumer.handleUserListRequest("");

        verify(kafkaTemplate).send(eq("user.error.response"), contains("Error fetching all users: Database error"));
    }
}