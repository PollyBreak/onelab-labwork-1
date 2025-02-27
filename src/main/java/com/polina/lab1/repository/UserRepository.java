package com.polina.lab1.repository;

import com.polina.lab1.dto.UserDTO;

import java.util.List;

public interface UserRepository {
    void save(UserDTO userDTO);
    void delete(Long userId);
    UserDTO findById(Long userId);
    List<UserDTO> findAll();
    UserDTO findByUsername(String username);
    UserDTO findByEmail(String email);
}
