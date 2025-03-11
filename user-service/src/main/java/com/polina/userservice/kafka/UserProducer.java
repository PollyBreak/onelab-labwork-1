package com.polina.userservice.kafka;

import com.polina.userservice.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserProducer {
    @Autowired
    private KafkaTemplate<String, UserDTO> kafkaTemplate;

    public void publishUserCreated(UserDTO user) {
        kafkaTemplate.send("user.created", user);
    }
}
