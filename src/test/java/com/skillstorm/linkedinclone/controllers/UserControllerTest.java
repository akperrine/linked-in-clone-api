package com.skillstorm.linkedinclone.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillstorm.linkedinclone.models.User;
import com.skillstorm.linkedinclone.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    private List<User> mockUsers;

    @BeforeEach
    void setUp() {
        mockUsers = new ArrayList<>();
        mockUsers.add(new User("test1.com", "testing"));
        mockUsers.add(new User("test2.com", "testing testing"));
    }

//    @Test
//    void testFindAllUsers() throws Exception {
//        when(userService.findAllUsers()).thenReturn(mockUsers);
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$").isArray());
//    }

//    @Test
//    void testAddNewUser() throws Exception {
//        User newUser = new User("newuser@test.com", "newpassword");
//        ResponseEntity<String> successResponse = ResponseEntity.ok("User created successfully");
//
//        when(userService.addNewUser(any(User.class))).thenReturn(ResponseEntity.ok(newUser));
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/users/register")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(asJsonString(newUser)))
//                .andExpect(status().isOk())
//                .andExpect(content().string("User created successfully"));
//    }


    private String asJsonString(Object obj) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}