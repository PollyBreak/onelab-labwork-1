package com.polina.userservice.service;

import com.polina.userservice.dto.UserPreferencesDTO;
import com.polina.userservice.entity.User;
import com.polina.userservice.kafka.KafkaProducer;
import com.polina.userservice.repository.UserRepository;
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


    public void addUserPreferences(UserPreferencesDTO preferencesDTO) {
        User user = userRepository.findById(preferencesDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User with ID " +
                        preferencesDTO.getUserId() + " does not exist."));
        List<String> updatedPreferences = new ArrayList<>
                (user.getFavoriteIngredients() != null? user.
                        getFavoriteIngredients():new ArrayList<>());
        updatedPreferences.addAll(preferencesDTO.getFavoriteIngredients());
        user.setFavoriteIngredients(new ArrayList<>(new HashSet<>(updatedPreferences)));
        userRepository.save(user);

        kafkaProducer.sendUserPreferencesUpdate(preferencesDTO.getUserId());
    }

    public void removeUserPreferences(Long userId, List<String> ingredientsToRemove) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId +
                        " does not exist."));
        List<String> updatedPreferences = new ArrayList<>(user.getFavoriteIngredients());
        updatedPreferences.removeAll(ingredientsToRemove);
        user.setFavoriteIngredients(updatedPreferences);
        userRepository.save(user);

        kafkaProducer.sendUserPreferencesUpdate(userId);
    }



    public UserPreferencesDTO getUserPreferences(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId +
                        " does not exist."));
        return new UserPreferencesDTO(user.getId(), user.getFavoriteIngredients());
    }
}
