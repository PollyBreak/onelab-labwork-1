package com.polina.userservice.service;

import com.polina.userservice.client.AuthClient;
import com.polina.userservice.entity.User;
import com.polina.userservice.repository.UserRepository;
import com.polina.dto.AuthRequest;
import com.polina.dto.UserDTO;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;

    private final AuthClient authClient;
    private final KafkaTemplate<String, Long> kafkaTemplate;

    public UserService(UserRepository userRepository,
                       AuthClient authClient,
                       KafkaTemplate<String, Long> kafkaTemplate) {
        this.userRepository = userRepository;
        this.authClient = authClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    public String registerUser(AuthRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already taken!");
        }
        authClient.register(new AuthRequest(request.getUsername(), request.getPassword()));

        User user = new User();
        user.setUsername(request.getUsername());
        user.setActive(true);
        userRepository.save(user);

        return "User registered successfully!";
    }

    public UserDTO findUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + id + " was not found."));
        return new UserDTO(user.getId(), user.getUsername(), user.getFavoriteIngredients());
    }


    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserDTO(user.getId(), user.getUsername(), user.getFavoriteIngredients()))
                .collect(Collectors.toList());
    }

    public void deleteUser(Long id, String token) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User with ID " + id + " does not exist.");
        }
        authClient.deleteUser(token, id);
        //authClient.deleteUser(token.replace("Bearer ", ""), id);

        userRepository.deleteById(id);

        kafkaTemplate.send("user-deleted-topic", id);
    }

}
