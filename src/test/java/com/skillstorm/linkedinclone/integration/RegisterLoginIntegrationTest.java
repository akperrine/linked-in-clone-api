package com.skillstorm.linkedinclone.integration;

import com.skillstorm.linkedinclone.dtos.LoginDto;
import com.skillstorm.linkedinclone.dtos.UserAuthDto;
import com.skillstorm.linkedinclone.models.User;
import com.skillstorm.linkedinclone.repositories.UserRepository;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RegisterLoginIntegrationTest {
 //** This integration test requires operating db
    @LocalServerPort
    private int port;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    private TestRestTemplate restTemplate;
    private HttpHeaders headers;

    @BeforeEach
    public void setup() {
        restTemplate = new TestRestTemplate();
        headers = new HttpHeaders();
    }

    @AfterEach
    public void cleanupUser() {
        Optional<User> userToDelete = userRepository.findByEmail("test@user123.com");
                if(userToDelete.isPresent()) {
                    userRepository.deleteById(userToDelete.get().getId());
                }
    }
    @Test
//    @Transactional
    public void testRegistrationAndLogin() {
        String baseUrl = "http://localhost:" + port;
        User newUser = new User("test@user123.com", "password");

        // Fail login before register
        LoginDto loginDto = new LoginDto("test@user123.com", "password");
        HttpEntity<LoginDto> preRegisterLogin = new HttpEntity<>(loginDto, headers);
        ResponseEntity<UserAuthDto> firstLoginResponse = restTemplate.exchange(
                baseUrl + "/users/login", HttpMethod.POST, preRegisterLogin, UserAuthDto.class);

//        verify(userRepository, never()).save(any(User.class));

        // Register a new user
        HttpEntity<User> registrationRequest = new HttpEntity<>(newUser, headers);
        ResponseEntity<UserAuthDto> registrationResponse = restTemplate.exchange(
                baseUrl + "/users/register", HttpMethod.POST, registrationRequest, UserAuthDto.class);

        // Check if registration was successful
        assertEquals(HttpStatus.OK, registrationResponse.getStatusCode());

        // Register an already used user
        HttpEntity<User> postRegistrationRequest = new HttpEntity<>(newUser, headers);
        ResponseEntity<UserAuthDto> postRegistrationResponse = restTemplate.exchange(
                baseUrl + "/users/register", HttpMethod.POST, postRegistrationRequest, UserAuthDto.class);

        // Check if registration was unsuccessful
        assertEquals(HttpStatus.CONFLICT, postRegistrationResponse.getStatusCode());

        // Log in with the registered user
        HttpEntity<LoginDto> loginRequest = new HttpEntity<>(loginDto, headers);
        ResponseEntity<UserAuthDto> loginResponse = restTemplate.exchange(
                baseUrl + "/users/login", HttpMethod.POST, loginRequest, UserAuthDto.class);

        // Check if login was successful
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());



    }

}
