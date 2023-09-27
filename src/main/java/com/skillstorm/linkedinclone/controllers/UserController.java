package com.skillstorm.linkedinclone.controllers;

import com.skillstorm.linkedinclone.dtos.UserAuthDto;
import com.skillstorm.linkedinclone.dtos.FollowDto;
import com.skillstorm.linkedinclone.dtos.LoginDto;
import com.skillstorm.linkedinclone.exceptions.UserNotFoundException;
import com.skillstorm.linkedinclone.models.User;
import com.skillstorm.linkedinclone.security.SecurityConstants;
import com.skillstorm.linkedinclone.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class UserController {

    @Autowired
    UserService userService;

    @Value("${app.jwt.expiration.minutes}")
    private Long jwtExpiration;

    @GetMapping
    public ResponseEntity<List<UserAuthDto>> findAllUsers() {
        List<UserAuthDto> results = userService.findAllUsers();
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    @PostMapping(value = {"/search/{nameQuery}", "/search"})
    public ResponseEntity<?> findUserBySearchName(@PathVariable(required = false) String nameQuery) {
        List<UserAuthDto> results;
        if(nameQuery == null) {
            results = userService.findAllUsers();
        }else {
            results = userService.searchByFirstAndLastName(nameQuery);
        }
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

    @PutMapping("/upload")
    public ResponseEntity<?> restLogin(@RequestParam("file") MultipartFile file, @RequestParam("email") String email){

        if (file.isEmpty()) {
            return new ResponseEntity<>("Please select a file to upload.", HttpStatus.BAD_REQUEST);
        }

        try {
            byte[] byteArray = file.getBytes();
            User userData = new User(email, byteArray);
            ResponseEntity result = userService.updateUserProfileImage(userData);
            return result;
        } catch (IOException e) {
            return new ResponseEntity<>("Failed to read the file: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<UserAuthDto> login(@CookieValue(name = "auth-cookie", required = false) String accessToken,
                                             @RequestBody LoginDto loginDto) {
        HttpHeaders responseHeaders = new HttpHeaders();

        if(loginDto.getEmail() != null && loginDto.getPassword() != null) {
            User user = userService.findUserByEmail(loginDto.getEmail());
            if(user != null) {
                String token = userService.getToken(loginDto.getEmail(), loginDto.getPassword());

                userService.addAccessTokenCookie(responseHeaders, token, jwtExpiration);
                UserAuthDto authDto = userService.setAuthResponseWithUserData(user);
                return ResponseEntity.ok().headers(responseHeaders).body(authDto);
            }
        } else if(accessToken!= null) {
            String email = userService.jwtGenerator.getUsernameFromJWT(accessToken);
            User user = userService.findUserByEmail(email);
            if (user != null) {
                UserAuthDto authDto = userService.setAuthResponseWithUserData(user);
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

    @GetMapping("/following/{email}")
    public ResponseEntity<?> getFollowing(@PathVariable String email){
        return userService.getFollowing(email);
    }

    @GetMapping("/follower/{email}")
    public ResponseEntity<?> getFollowersOf(@PathVariable String email){
        return userService.getFollower(email);
    }
}   
    
