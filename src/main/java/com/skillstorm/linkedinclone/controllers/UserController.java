package com.skillstorm.linkedinclone.controllers;

import com.skillstorm.linkedinclone.dtos.AuthResponseDto;
import com.skillstorm.linkedinclone.dtos.LoginDto;
import com.skillstorm.linkedinclone.exceptions.UserNotFoundException;
import com.skillstorm.linkedinclone.models.User;
import com.skillstorm.linkedinclone.security.JWTGenerator;
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
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    JWTGenerator jwtGenerator;

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
    public ResponseEntity<?> addNewUser(@RequestBody User userData) {
        String password = userData.getPassword();
        ResponseEntity<?> response = userService.addNewUser(userData);
        System.out.println(userData.toString());
        return response;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginDto loginDto) {
        HttpHeaders responseHeaders = new HttpHeaders();

        if(loginDto.getEmail() != null && loginDto.getPassword() != null) {
            User user = userService.findUserByEmail(loginDto.getEmail());
            if(user != null) {
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginDto.getEmail(),
                                loginDto.getPassword()
                        ));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                String token = jwtGenerator.generateToken(authentication);

                addAccessTokenCookie(responseHeaders, token, jwtExpiration);
                AuthResponseDto authDto = userService.setAuthResponseWithUserData(user);
                return ResponseEntity.ok().headers(responseHeaders).body(authDto);
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    //duration to be in minutes
    private void addAccessTokenCookie(HttpHeaders httpHeaders, String token, long duration){

        httpHeaders.add(HttpHeaders.SET_COOKIE, createAccessCookie(token, duration*60).toString());
    }
    private HttpCookie createAccessCookie(String token, long duration){
        return ResponseCookie.from("auth-cookie", token).httpOnly(true).path("/").maxAge(duration).build();
    }
}   
    
