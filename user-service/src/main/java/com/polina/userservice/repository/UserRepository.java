package com.polina.userservice.repository;

import com.polina.userservice.dto.UserDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserDTO, Long>{
    Optional<UserDTO> findByUsername(String username);
    Optional<UserDTO> findByEmail(String email);
}
