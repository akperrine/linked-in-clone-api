package com.skillstorm.linkedinclone.services;

import com.skillstorm.linkedinclone.dtos.UserAuthDto;
import com.skillstorm.linkedinclone.exceptions.UserNotFoundException;
import com.skillstorm.linkedinclone.models.User;
import com.skillstorm.linkedinclone.repositories.UserRepository;
import com.skillstorm.linkedinclone.security.SecurityConstants;
import com.skillstorm.linkedinclone.security.JWTGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@SpringBootTest
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    JWTGenerator jwtGenerator;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindUserByEmailUserExists() {
        String userEmail = "test@user.com";
        User mockUser = new User("test@user.com", "password", "John");

        when(userRepository.findByEmail("test@user.com")).thenReturn(Optional.of(mockUser));

        User foundUser = userService.findUserByEmail(userEmail);

        verify(userRepository, times(1)).findByEmail(userEmail);

        assertNotNull(foundUser);
        assertEquals(mockUser, foundUser);
    }

    @Test
    void testFindUserByEmailUserDoesNotExist() {
        String userEmail = "nonexistent@user.com";

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findUserByEmail(userEmail));

        verify(userRepository, times(1)).findByEmail(userEmail);
    }

    @Test
    void testAddNewUser_WhenUserExists() {
        User existingUser = new User("test@user.com", "password");
        when(userRepository.findByEmail(existingUser.getEmail())).thenReturn(Optional.of(existingUser));

        ResponseEntity<?> responseEntity = userService.addNewUser(existingUser);

        // Verify that the repository's findByEmail method is called
        verify(userRepository, times(1)).findByEmail(existingUser.getEmail());

        assertEquals(HttpStatus.CONFLICT.CONFLICT, responseEntity.getStatusCode());
    }

    @Test
    void testAddNewUser_WhenUserDoesNotExist() {
        User newUser = new User("new@test.com", "password");
        when(userRepository.findByEmail(newUser.getEmail())).thenReturn(Optional.empty());

        ResponseEntity<UserAuthDto> expectedResponse = ResponseEntity.ok().body(new UserAuthDto());
        when(userRepository.save(newUser)).thenReturn(newUser);

        ResponseEntity<?> responseEntity = userService.addNewUser(newUser);

        // Verify that the repository's findByEmail method is called
        verify(userRepository, times(1)).findByEmail(newUser.getEmail());

        // Verify that the repository's save method is called
        verify(userRepository, times(1)).save(newUser);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void testUpdateUser_WhenUserExists() {
        User existingUser = new User("test@user.com", "password");
        User updatedUser = new User("test@user.com", "newPassword", "John");

        when(userRepository.findByEmail(existingUser.getEmail())).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        ResponseEntity<?> responseEntity = userService.updateUser(updatedUser);

        verify(userRepository, times(1)).findByEmail(existingUser.getEmail());
        verify(userRepository, times(1)).save(existingUser);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testGetToken() {
        String username = "testUser";
        String password = "testPassword";
        Authentication mockAuthentication = mock(Authentication.class);
        String expectedToken = "mocked-token";

        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password)))
                .thenReturn(mockAuthentication);
        when(jwtGenerator.generateToken(mockAuthentication)).thenReturn(expectedToken);

        String token = userService.getToken(username, password);

        assertEquals(expectedToken, token);
    }

    @Test
    public void testAddAccessTokenCookie() {
        UserService userService = new UserService();
        HttpHeaders httpHeaders = new HttpHeaders();
        String token = "test-token";
        long duration = SecurityConstants.JWT_EXPIRATION;
        ResponseCookie expectedCookie = ResponseCookie.from("auth-cookie", token)
                .httpOnly(true)
                .path("/")
                .maxAge(duration * 60)
                .build();

        userService.addAccessTokenCookie(httpHeaders, token, duration);

        assertEquals(expectedCookie.toString(), httpHeaders.getFirst(HttpHeaders.SET_COOKIE));
    }

    @Test
    public void testCreateAccessCookie() {
        UserService userService = new UserService();
        String token = "test-token";
        long duration = 3600L;
        ResponseCookie expectedCookie = ResponseCookie.from("auth-cookie", token)
                .httpOnly(true)
                .path("/")
                .maxAge(duration)
                .build();

        HttpCookie cookie = userService.createAccessCookie(token, duration);

        assertEquals(expectedCookie, cookie);
    }




}
