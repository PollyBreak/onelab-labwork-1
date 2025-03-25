//package com.polina.userservice.controller;
//
//import com.polina.userservice.dto.UserDTO;
//import com.polina.userservice.service.UserService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.ResponseEntity;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class UserControllerTest {
//    @InjectMocks
//    private UserController userController;
//    @Mock
//    private UserService userService;
//    private UserDTO mockUser;
//
//    @BeforeEach
//    void setUp() {
//        mockUser = new UserDTO(1L, "testUser",
//                "test@example.com", "password123");
//    }
//
//    @Test
//    void testRegisterUser() {
//        when(userService.registerUser(mockUser)).thenReturn("User registered successfully");
//
//        String response = userController.registerUser(mockUser);
//
//        assertEquals("User registered successfully", response);
//        verify(userService, times(1)).registerUser(mockUser);
//    }
//
//    @Test
//    void testGetUserByUsername() {
//        when(userService.getUserByUsername("testUser")).thenReturn(mockUser);
//        UserDTO response = userController.getUserByUsername("testUser");
//        assertNotNull(response);
//        assertEquals("testUser", response.getUsername());
//    }
//
//    @Test
//    void testGetUserById() {
//        when(userService.findUserById(1L)).thenReturn(mockUser);
//        ResponseEntity<UserDTO> response = userController.getUserById(1L);
//        assertNotNull(response.getBody());
//        assertEquals(1L, response.getBody().getId());
//    }
//
//    @Test
//    void testGetAllUsers() {
//        List<UserDTO> users = List.of(mockUser);
//        when(userService.getAllUsers()).thenReturn(users);
//        ResponseEntity<List<UserDTO>> response = userController.getAllUsers();
//        assertNotNull(response.getBody());
//        assertEquals(1, response.getBody().size());
//    }
//
//    @Test
//    void testDeleteUser() {
//        doNothing().when(userService).deleteUser(1L);
//        ResponseEntity<String> response = userController.deleteUser(1L);
//        assertEquals("User deleted successfully", response.getBody());
//        verify(userService, times(1)).deleteUser(1L);
//    }
//
//    @Test
//    void testGetUserIdByUsername() {
//        when(userService.getUserIdByUsername("testUser")).thenReturn(1L);
//        ResponseEntity<Long> response = userController.getUserIdByUsername("testUser");
//        assertEquals(1L, response.getBody());
//    }
//}
