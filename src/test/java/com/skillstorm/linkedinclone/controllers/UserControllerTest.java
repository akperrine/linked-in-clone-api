package com.skillstorm.linkedinclone.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillstorm.linkedinclone.dtos.UserAuthDto;
import com.skillstorm.linkedinclone.models.User;
import com.skillstorm.linkedinclone.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    public void testSuccesfulRegister() throws Exception {
        String email = "test@user.com";
        String password = "password";

        User mockUser = new User("test@user.com", "password");
        UserAuthDto mockAuthDto = new UserAuthDto();
        mockAuthDto.setEmail("test@user.com");
        when(userService.addNewUser(mockUser)).thenReturn(ResponseEntity.status(HttpStatus.OK).body(mockAuthDto));

        mockMvc.perform(MockMvcRequestBuilders.post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"" + email + "\", \"password\": \"" + password + "\"}"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value("test@user.com"));
    }


    @Test
    @WithMockUser
    public void testSuccessfulLogin() throws Exception {
        String email = "test@user.com";
        String password = "password";

        User mockUser = new User("test@user.com", "password");
        UserAuthDto authDto = new UserAuthDto();
        authDto.setEmail("test@user.com");

        when(userService.findUserByEmail("test@user.com")).thenReturn(mockUser);
        when(userService.setAuthResponseWithUserData(mockUser)).thenReturn(authDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"" + email + "\", \"password\": \"" + password + "\"}"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value("test@user.com"));

    }

    @Test
    @WithMockUser
    public void testUnauthorizedLogin() throws Exception {
        String email = "test@user.com";
        String password = "password";

        when(userService.findUserByEmail("test@user.com")).thenReturn(null);

        // Perform a POST request to the /login endpoint without providing email and password
        mockMvc.perform(MockMvcRequestBuilders.post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"" + email + "\", \"password\": \"" + password + "\"}"))
                .andExpect(status().isUnauthorized());
    }


}