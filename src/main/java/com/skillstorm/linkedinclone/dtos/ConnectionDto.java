package com.skillstorm.linkedinclone.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConnectionDto {
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
    //TODO do we need to send these back to user?
    //private Set<User> connectionsOf;
}
