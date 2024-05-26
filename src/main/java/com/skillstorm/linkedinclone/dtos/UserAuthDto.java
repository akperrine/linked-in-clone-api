package com.skillstorm.linkedinclone.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Lob;
import java.util.Set;

@Data
@NoArgsConstructor
public class UserAuthDto {
    private int id;
    private String email;
    private String firstName;
    private String lastName;
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
    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    private byte[] imageUrl;
    //TODO do we need to send these back to user?
    private Set<ConnectionDto> following;
    private Set<ConnectionDto> follower;
}
