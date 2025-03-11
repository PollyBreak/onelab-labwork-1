package com.polina.userservice;

import com.polina.userservice.dto.UserDTO;
import com.polina.userservice.kafka.UserProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class UserProducerTest {

    @InjectMocks
    private UserProducer userProducer;

    @Mock
    private KafkaTemplate<String, UserDTO> kafkaTemplate;

    private UserDTO testUser;

    @BeforeEach
    void setUp() {
        testUser = new UserDTO();
        testUser.setUsername("testUser");
        testUser.setEmail("test@example.com");
    }

    @Test
    void publishUserCreated_Success() {
        userProducer.publishUserCreated(testUser);

        verify(kafkaTemplate).send(eq("user.created"), eq(testUser));
    }
}