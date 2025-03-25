package com.polina.userservice.kafka;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaProducerTest {

    @InjectMocks
    private KafkaProducer kafkaProducer;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @BeforeEach
    void setUp() {
        kafkaProducer = new KafkaProducer(kafkaTemplate);
    }

    @Test
    void testSendUserPreferencesUpdate() {
        Long userId = 123L;
        String expectedMessage = "User " + userId + " updated preferences";

        kafkaProducer.sendUserPreferencesUpdate(userId);

        verify(kafkaTemplate, times(1))
                .send("user-preferences-topic", expectedMessage);
    }
}
