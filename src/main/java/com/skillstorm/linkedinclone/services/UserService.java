package com.skillstorm.linkedinclone.services;

import com.skillstorm.linkedinclone.dtos.ConnectionDto;
import com.skillstorm.linkedinclone.dtos.UserAuthDto;
import com.skillstorm.linkedinclone.dtos.FollowDto;
import com.skillstorm.linkedinclone.exceptions.UserNotFoundException;
import com.skillstorm.linkedinclone.models.Post;
import com.skillstorm.linkedinclone.models.User;
import com.skillstorm.linkedinclone.repositories.UserRepository;
import com.skillstorm.linkedinclone.security.JWTGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    public JWTGenerator jwtGenerator;

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<UserAuthDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        return userListToUserAuthDto(users);
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
            UserAuthDto newUserDto = setAuthResponseWithUserData(newUser);
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

    public ResponseEntity<?> updateUserProfileImage(User userData) {
        User user = userRepository.findByEmail(userData.getEmail()).orElse(null);
        if(user == null){
            return ResponseEntity.badRequest().body("User does not exist!");
        }else{
            System.out.println("hits");
            user.setImageUrl(userData.getImageUrl());

            userRepository.save(user);
            return ResponseEntity.ok().body(user);
        }
    }

    public UserAuthDto setAuthResponseWithUserData(User user) {
        UserAuthDto authDto = new UserAuthDto();

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
        authDto.setConnections(userSetToUserAuthDto(user.getConnections()));

        return authDto;
    }

    public ConnectionDto setConnectionDtoWithUserData(User user) {
        ConnectionDto authDto = new ConnectionDto();

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

    public ResponseEntity<?> getAllLikesByEmail(String email) {
        if(userRepository.findByEmail(email).isPresent()){

            MatchOperation matchOperation = Aggregation.match(new Criteria("_id.email").is(email));
            ProjectionOperation projectionOperation = Aggregation.project().and(ConvertOperators.ToObjectId.toObjectId("$_id.postId")).as("postId");
            LookupOperation lookupOperation = LookupOperation.newLookup()
                    .from("posts")
                    .localField("postId")
                    .foreignField("_id")
                    .as("likedPost");

            Aggregation aggregation = Aggregation.newAggregation(
                    //lookupOperation
                    matchOperation,
                    projectionOperation,
                    lookupOperation,
                    Aggregation.unwind("$likedPost"),
                    Aggregation.replaceRoot("$likedPost")
            );

            List<Post> results = mongoTemplate.aggregate(aggregation, "likes", Post.class).getMappedResults();
            return new ResponseEntity<>(results, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<?> followUser(FollowDto followDto) {
        User user1 = userRepository.findByEmail(followDto.getUserEmail()).orElse(null);
        User user2 = userRepository.findByEmail(followDto.getConnectionEmail()).orElse(null);
        System.out.println(user1 + " " + user2);
        if(user1!=null && user2!=null){
            if(followDto.isFollow()){
                user1.addConnection(user2);
            }
            // Remove user if boolean is false
            else{
                user1.removeConnection(user2);
            }
            userRepository.save(user1);
            //userRepository.save(user2);
            return new ResponseEntity<>(user1.getConnections(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<?> getFollower(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if(user!=null){
            return new ResponseEntity<>(user.getConnections(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<?> getFollowerOf(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if(user!=null){
            return new ResponseEntity<>(user.getConnectionsOf(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    public List<UserAuthDto> searchByFirstAndLastName(String name) {
        List<User> users = userRepository.searchUsersByFirstNameOrLastName(name);
        return userListToUserAuthDto(users);
    }

    private List<UserAuthDto> userListToUserAuthDto(List<User> users) {
        return users.stream()
                .map(user -> setAuthResponseWithUserData(user))
                .collect(Collectors.toList());
    }
    private Set<ConnectionDto> userSetToUserAuthDto(Set<User> users) {
        return users.stream()
                .map(user -> setConnectionDtoWithUserData(user))
                .collect(Collectors.toSet());
    }
}
