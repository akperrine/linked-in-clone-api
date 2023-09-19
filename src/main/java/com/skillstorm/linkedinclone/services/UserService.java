package com.skillstorm.linkedinclone.services;

import com.skillstorm.linkedinclone.dtos.AuthResponseDto;
import com.skillstorm.linkedinclone.exceptions.UserNotFoundException;
import com.skillstorm.linkedinclone.models.User;
import com.skillstorm.linkedinclone.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

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
            userRepository.save(userData);
        }

        return ResponseEntity.ok().build();
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
        authDto.setConnections(user.getConnections());

        return authDto;
    }

    public ResponseEntity<?> updateUser(User userData) {
        User user = userRepository.findByEmail(userData.getEmail()).orElse(null);

        if(user== null){
            return ResponseEntity.badRequest().body("User does not exist!");
        }else{
            user.setFirstName(userData.getFirstName());
            userRepository.save(user);
        }
        return ResponseEntity.ok().body(user);
    }
}
