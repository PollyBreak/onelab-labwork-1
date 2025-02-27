package com.polina.lab1.repository;

import com.polina.lab1.dto.UserDTO;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class UserRepositoryImpl implements UserRepository{

    private final List<UserDTO> userRepository = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(1l);

    @Override
    public void save(UserDTO userDTO) {
        if (userDTO.getId()==null){
            userDTO.setId(idCounter.getAndIncrement());
            userRepository.add(userDTO);
        } else {
            delete(userDTO.getId());
            userRepository.add(userDTO);
        }
    }

    @Override
    public void delete(Long userId) {
        userRepository.removeIf(user-> user.getId().equals(userId));
    }

    @Override
    public UserDTO findById(Long userId) {
        return userRepository
                .stream()
                .filter(user-> user.getId() != null && user.getId().equals(userId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<UserDTO> findAll() {
        return new ArrayList<>(userRepository);
    }

    @Override
    public UserDTO findByUsername(String username) {
        return userRepository
                .stream()
                .filter(user-> user.getId() != null && user.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    @Override
    public UserDTO findByEmail(String email) {
        return userRepository
                .stream()
                .filter(user-> user.getId()!= null && user.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }
}
