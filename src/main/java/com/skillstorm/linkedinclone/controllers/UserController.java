package com.skillstorm.linkedinclone.controllers;

import com.skillstorm.linkedinclone.dtos.AuthResponseDto;
import com.skillstorm.linkedinclone.dtos.FollowDto;
import com.skillstorm.linkedinclone.dtos.LoginDto;
import com.skillstorm.linkedinclone.exceptions.UserNotFoundException;
import com.skillstorm.linkedinclone.models.User;
import com.skillstorm.linkedinclone.security.JWTGenerator;
import com.skillstorm.linkedinclone.security.SecurityConstants;
import com.skillstorm.linkedinclone.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class UserController {

    @Autowired
    UserService userService;

    @Value("${app.jwt.expiration.minutes}")
    private Long jwtExpiration;

    @GetMapping
    public ResponseEntity<List<User>> findAllUsers() {
        List<User> results = userService.findAllUsers();
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    @GetMapping("/{email}")
    public ResponseEntity<User> findAllUsers(@PathVariable String email) throws UserNotFoundException {
        try{
            User user = userService.findUserByEmail(email);
            return ResponseEntity.status(200).body(user);
        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(null);
        }

    }

    @PostMapping("/register")
    public ResponseEntity<?> addNewUser(@RequestBody User registerData) {
        ResponseEntity<?> response = userService.addNewUser(new User(registerData.getEmail(), registerData.getPassword()));

        HttpHeaders responseHeaders = new HttpHeaders();
        if(response.getStatusCode() != HttpStatus.BAD_REQUEST) {
            String token = userService.getToken(registerData.getEmail(), registerData.getPassword());
            userService.addAccessTokenCookie(responseHeaders, token, SecurityConstants.JWT_EXPIRATION);
            return ResponseEntity.ok().headers(responseHeaders).body(response.getBody());
        }

        return response;
    }
    @PutMapping("/update")
    public ResponseEntity<?> restLogin(@RequestBody User userData){
        return userService.updateUser(userData);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@CookieValue(name = "auth-cookie", required = false) String accessToken,
                                                 @RequestBody LoginDto loginDto) {
        HttpHeaders responseHeaders = new HttpHeaders();

        if(loginDto.getEmail() != null && loginDto.getPassword() != null) {
            User user = userService.findUserByEmail(loginDto.getEmail());
            if(user != null) {
                String token = userService.getToken(loginDto.getEmail(), loginDto.getPassword());

                userService.addAccessTokenCookie(responseHeaders, token, jwtExpiration);
                AuthResponseDto authDto = userService.setAuthResponseWithUserData(user);
                return ResponseEntity.ok().headers(responseHeaders).body(authDto);
            }
        } else if(accessToken!= null) {
            String email = userService.jwtGenerator.getUsernameFromJWT(accessToken);
            User user = userService.findUserByEmail(email);
            if (user != null) {
                AuthResponseDto authDto = userService.setAuthResponseWithUserData(user);
                return ResponseEntity.ok().body(authDto);
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    //TODO we probably need to add another get mapping for "/like"
    //TODO which returns the current user's liked post, using Authentication to retrieve user's email

    @GetMapping("/likes/{email}")
    public ResponseEntity<?> getAllLikesByEmail(@PathVariable String email){
        return userService.getAllLikesByEmail(email);
    }

    @PostMapping("/follow")
    public ResponseEntity<?> followUser(@RequestBody FollowDto followDto){
        return userService.followUser(followDto);
    }

    @GetMapping("/follower/{email}")
    public ResponseEntity<?> getFollower(@PathVariable String email){
        return userService.getFollower(email);
    }

    @GetMapping("/followerOf/{email}")
    public ResponseEntity<?> getFollowerOf(@PathVariable String email){
        return userService.getFollowerOf(email);
    }
}   
    
