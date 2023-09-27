package com.skillstorm.linkedinclone.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false)
    private String email;
    private String password;
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
    private boolean firstLogin = true;
    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    private byte[] imageUrl;

    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade = CascadeType.REMOVE
    )
    @JsonIgnore
    @JoinTable(
            name = "user_connections",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "connection_id")
    )
    private Set<User> following;

    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade = CascadeType.REMOVE
    )
    @JsonIgnore
    @JoinTable(
            name = "user_connections",
            joinColumns = @JoinColumn(name = "connection_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> follower;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
    public User(String email, String password, String firstName) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
    }

    public User(String email, byte[] imageUrl) {
        this.imageUrl = imageUrl;
        this.email = email;
    }


    public void addFollowing(User connection){
        this.following.add(connection);
        //connection.getConnectionsOf().add(this);
    }

    public void removeFollowing(User connection) {
        User user = this.following.stream().filter(u -> u.getId() == connection.getId()).findFirst().orElse(null);
        if(user!= null && user.equals(connection)){
            this.following.remove(connection);
            //connection.getConnectionsOf().remove(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && firstLogin == user.firstLogin && Objects.equals(email, user.email) && Objects.equals(password, user.password) && Objects.equals(firstName, user.firstName) && Objects.equals(lastName, user.lastName) && Objects.equals(imageUrl, user.imageUrl) && Objects.equals(headline, user.headline) && Objects.equals(country, user.country) && Objects.equals(city, user.city) && Objects.equals(company, user.company) && Objects.equals(industry, user.industry) && Objects.equals(college, user.college) && Objects.equals(website, user.website) && Objects.equals(about, user.about) && Objects.equals(role, user.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, password, firstName, lastName, imageUrl, headline, country, city, company, industry, college, website, about, role, firstLogin);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", headline='" + headline + '\'' +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", company='" + company + '\'' +
                ", industry='" + industry + '\'' +
                ", college='" + college + '\'' +
                ", website='" + website + '\'' +
                ", about='" + about + '\'' +
                ", role='" + role + '\'' +
                ", firstLogin=" + firstLogin +
                '}';
    }
}
