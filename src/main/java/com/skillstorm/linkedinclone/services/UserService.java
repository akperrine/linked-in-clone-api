package com.skillstorm.linkedinclone.services;

import com.skillstorm.linkedinclone.dtos.AuthResponseDto;
import com.skillstorm.linkedinclone.exceptions.UserNotFoundException;
import com.skillstorm.linkedinclone.models.User;
import com.skillstorm.linkedinclone.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username).orElseThrow(()-> new UsernameNotFoundException(username+ " not found!"));
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), user.getAuthorities());
    }
}
