package com.polina.userservice.service;

import com.polina.userservice.entity.User;
import com.polina.userservice.kafka.KafkaProducer;
import com.polina.userservice.repository.UserRepository;
import com.polina.dto.UserPreferencesDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class UserPreferencesService {
    private KafkaProducer kafkaProducer;
    private final UserRepository userRepository;

    @Autowired
    public UserPreferencesService(UserRepository userRepository, KafkaProducer kafkaProducer) {
        this.userRepository = userRepository;
        this.kafkaProducer=kafkaProducer;
    }


    public void addUserPreferences(Long userId, UserPreferencesDTO preferencesDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " +
                        userId + " does not exist."));
        List<String> updatedPreferences = new ArrayList<>
                (user.getFavoriteIngredients() != null? user.
                        getFavoriteIngredients():new ArrayList<>());

        updatedPreferences.addAll(preferencesDTO.getIngredients());
        user.setFavoriteIngredients(new ArrayList<>(new HashSet<>(updatedPreferences)));
        userRepository.save(user);

        kafkaProducer.sendUserPreferencesUpdate(userId);
    }

    public void removeUserPreferences(Long userId, UserPreferencesDTO preferencesDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId +
                        " does not exist."));
        List<String> updatedPreferences = new ArrayList<>(user.getFavoriteIngredients());
        updatedPreferences.removeAll(preferencesDTO.getIngredients());
        user.setFavoriteIngredients(updatedPreferences);
        userRepository.save(user);

        kafkaProducer.sendUserPreferencesUpdate(userId);
    }



    public UserPreferencesDTO getUserPreferences(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId +
                        " does not exist."));
        return new UserPreferencesDTO(user.getFavoriteIngredients());
    }
}
