package com.skillstorm.linkedinclone.services;

import com.skillstorm.linkedinclone.dtos.AuthResponseDto;
import com.skillstorm.linkedinclone.exceptions.UserNotFoundException;
import com.skillstorm.linkedinclone.models.User;
import com.skillstorm.linkedinclone.repositories.UserRepository;
import com.skillstorm.linkedinclone.security.JWTGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JWTGenerator jwtGenerator;

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User findUserByEmail(String email) throws UserNotFoundException {
        return userRepository.findByEmail(email)
                .map(u -> u)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    public ResponseEntity<?> addNewUser(User userData) {
        if(userRepository.findByEmail(userData.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exist!");
        } else {
            userData.setPassword(passwordEncoder.encode(userData.getPassword()));
            userData.setRole("ROLE_USER");
            User newUser = userRepository.save(userData);
            System.out.println(newUser.toString());
            AuthResponseDto newUserDto = setAuthResponseWithUserData(newUser);
            return ResponseEntity.ok().body(newUserDto);
        }
    }

    public ResponseEntity<?> updateUser(User userData) {
        User user = userRepository.findByEmail(userData.getEmail()).orElse(null);

        if(user== null){
            return ResponseEntity.badRequest().body("User does not exist!");
        }else{
            user.setFirstName(userData.getFirstName());
            user.setLastName(userData.getLastName());
            user.setImageUrl(userData.getImageUrl());
            user.setHeadline(userData.getHeadline());
            user.setCountry(userData.getCountry());
            user.setCity(userData.getCity());
            user.setCompany(userData.getCompany());
            user.setIndustry(userData.getIndustry());
            user.setCollege(userData.getCollege());
            user.setWebsite(userData.getWebsite());
            user.setAbout(userData.getAbout());
            user.setFirstLogin(userData.isFirstLogin());
            user.setConnections(userData.getConnections());

            userRepository.save(user);
        }
        return ResponseEntity.ok().body(user);
    }

    public AuthResponseDto setAuthResponseWithUserData(User user) {
        AuthResponseDto authDto = new AuthResponseDto();

        authDto.setId(user.getId());
        authDto.setEmail(user.getEmail());
        authDto.setFirstName(user.getFirstName());
        authDto.setLastName(user.getLastName());
        authDto.setImageUrl(user.getImageUrl());
        authDto.setHeadline(user.getHeadline());
        authDto.setCountry(user.getCountry());
        authDto.setCity(user.getCity());
        authDto.setCompany(user.getCompany());
        authDto.setIndustry(user.getIndustry());
        authDto.setCollege(user.getCollege());
        authDto.setWebsite(user.getWebsite());
        authDto.setAbout(user.getAbout());
        authDto.setRole(user.getRole());
        authDto.setFirstLogin(user.isFirstLogin());
        authDto.setConnections(user.getConnections());

        return authDto;
    }
    public String getToken(String username, String password){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtGenerator.generateToken(authentication);
    }

    public void addAccessTokenCookie(HttpHeaders httpHeaders, String token, long duration){

        httpHeaders.add(HttpHeaders.SET_COOKIE, createAccessCookie(token, duration*60).toString());
    }
    private HttpCookie createAccessCookie(String token, long duration){
        return ResponseCookie.from("auth-cookie", token).httpOnly(true).path("/").maxAge(duration).build();
    }
}
