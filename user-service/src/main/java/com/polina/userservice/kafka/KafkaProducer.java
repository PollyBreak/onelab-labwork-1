package com.polina.userservice.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendUserPreferencesUpdate(Long userId) {
        String message = "User " + userId + " updated preferences";
        kafkaTemplate.send("user-preferences-topic", message);
    }
}
