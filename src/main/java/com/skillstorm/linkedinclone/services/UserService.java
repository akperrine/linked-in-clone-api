package com.skillstorm.linkedinclone.services;

import com.skillstorm.linkedinclone.models.User;
import com.skillstorm.linkedinclone.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public ResponseEntity<?> addNewUser(User userData) {
        if(userRepository.findByEmail(userData.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exist!");
        } else {
            userData.setPassword(passwordEncoder.encode(userData.getPassword()));
            userData.setRole("ROLE_USER");
            userRepository.save(userData);
        }

        return ResponseEntity.ok().build();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username).orElseThrow(()-> new UsernameNotFoundException(username+ " not found!"));
        //System.out.println(user.getUsername());
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), user.getAuthorities());
    }
}
