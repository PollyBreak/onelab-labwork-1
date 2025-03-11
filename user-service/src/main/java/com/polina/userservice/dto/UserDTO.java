package com.polina.userservice.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    @Column(unique = true, nullable = false)
    private String email;
}
