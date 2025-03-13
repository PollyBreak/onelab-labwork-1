package com.polina.authservice.client;

import com.polina.authservice.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "user-service")
public interface UserClient {

    @PostMapping("/users/register")
    String registerUser(@RequestBody UserDTO userDTO);

    @GetMapping("/users/by-username/{username}")
    UserDTO getUserByUsername(@PathVariable String username);

    @GetMapping("/users/id/{username}")
    Long getUserIdByUsername(@PathVariable String username);
}
