package com.skillstorm.linkedinclone.dtos;

import com.skillstorm.linkedinclone.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
public class AuthResponseDto {
    private int id;
    private String email;
    private String firstName;
    private String lastName;
    private String imageUrl;
    private String headline;
    private String country;
    private String city;
    private String company;
    private String industry;
    private String college;
    private String website;
    private String about;
    private String role;
    private boolean firstLogin;
    //TODO do we need to send these back to user?
    //private Set<User> connections;
    //private Set<User> connectionsOf;
}
