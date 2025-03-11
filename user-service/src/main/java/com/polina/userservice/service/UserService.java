package com.polina.userservice.service;


import com.polina.userservice.dto.UserDTO;
import com.polina.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository){
        this.userRepository=userRepository;
    }

    public void saveUser(UserDTO user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username '"+ user.getUsername() + "' is already taken!");
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email '" + user.getEmail()+ "' is already registered!");
        }
        userRepository.save(user);
    }

    public UserDTO findUserById(Long id) {
        UserDTO user = userRepository.findById(id)
                .orElseThrow(()->new NoSuchElementException("User with ID " + id + " was not found."));
        return user;
    }

    public Optional<UserDTO> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)){
            throw new NoSuchElementException("User with ID " + id + " does not exist.");
        }
        userRepository.deleteById(id);
    }


}
