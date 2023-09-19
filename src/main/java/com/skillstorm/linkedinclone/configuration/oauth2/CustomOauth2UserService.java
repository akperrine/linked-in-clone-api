package com.skillstorm.linkedinclone.configuration.oauth2;

import com.skillstorm.linkedinclone.models.CustomUserDetails;
import com.skillstorm.linkedinclone.models.User;
import com.skillstorm.linkedinclone.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CustomOauth2UserService extends DefaultOAuth2UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OAuth2UserInfoExtractor oAuth2UserInfoExtractor;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest){
        OAuth2User oAuth2User = super.loadUser(userRequest);
        CustomUserDetails customUserDetails = oAuth2UserInfoExtractor.extractUserInfo(oAuth2User);
        User user = addUser(customUserDetails);
        return customUserDetails;
    }

    private User addUser(CustomUserDetails customUserDetails){
        Optional<User> userOptional = userRepository.findByEmail(customUserDetails.getEmail());
        if(!userOptional.isPresent()) {
            User user = new User();
            user.setEmail(customUserDetails.getEmail());
            user.setRole("ROLE_USER");
            return userRepository.save(user);
        }else {
            return userOptional.get();
        }
    }
}
