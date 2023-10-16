package com.skillstorm.linkedinclone.services;

import com.skillstorm.linkedinclone.dtos.FollowDto;
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

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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

    @Test
    public void testGetFollowing() {
        // Define a sample email
        String email = "test@example.com";

        // Create a user with following
        User user = new User();
        Set<User> following = new HashSet<>();
        following.add(new User("follower1", "password"));
        following.add(new User("follower2", "password"));
        user.setFollowing(following);

        // Mock the userRepository behavior
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Call the method
        ResponseEntity<?> response = userService.getFollowing(email);

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testGetFollowingUserNotFound() {
        // Define a sample email
        String email = "nonexistent@example.com";

        // Mock the userRepository behavior to return an empty result
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Call the method
        ResponseEntity<?> response = userService.getFollowing(email);

        // Verify the response
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testFollowUser() {
        // Create a FollowDto with user and target emails
        FollowDto followDto = new FollowDto("user1@example.com", "user2@example.com", true);

        // Create user1 and user2
        User user1 = new User("user1@example.com", "password");
        user1.setFollowing(new HashSet<>());
        User user2 = new User("user2@example.com", "password");

        // Mock the userRepository behavior
        when(userRepository.findByEmail("user1@example.com")).thenReturn(Optional.of(user1));
        when(userRepository.findByEmail("user2@example.com")).thenReturn(Optional.of(user2));

        // Call the method
        ResponseEntity<?> response = userService.followUser(followDto);

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verify that user1 is now following user2
        assertTrue(user1.getFollowing().contains(user2));
    }

    @Test
    public void testUnfollowUser() {
        // Create a FollowDto with user and target emails for unfollowing
        FollowDto followDto = new FollowDto("user1@example.com", "user2@example.com", false);

        // Create user1 and user2
        User user1 = new User("user1@example.com", "password");
        user1.setFollowing(new HashSet<>());
        User user2 = new User("user2@example.com", "password");

        // Mock the userRepository behavior
        when(userRepository.findByEmail("user1@example.com")).thenReturn(Optional.of(user1));
        when(userRepository.findByEmail("user2@example.com")).thenReturn(Optional.of(user2));

        // Make user1 follow user2 initially
        user1.addFollowing(user2);

        // Call the method
        ResponseEntity<?> response = userService.followUser(followDto);

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verify that user1 is not following user2 anymore
        assertFalse(user1.getFollowing().contains(user2));
    }

    @Test
    public void testFollowUserUserNotFound() {
        // Create a FollowDto with a user that doesn't exist
        FollowDto followDto = new FollowDto("nonexistent@example.com", "user2@example.com", true);

        // Mock the userRepository behavior to return empty results
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Call the method
        ResponseEntity<?> response = userService.followUser(followDto);

        // Verify the response
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

}
