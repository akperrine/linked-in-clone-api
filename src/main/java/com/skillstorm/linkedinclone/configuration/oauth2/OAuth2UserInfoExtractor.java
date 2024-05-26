package com.skillstorm.linkedinclone.configuration.oauth2;

import com.skillstorm.linkedinclone.models.CustomUserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class OAuth2UserInfoExtractor {
    public CustomUserDetails extractUserInfo(OAuth2User oAuth2User){
        CustomUserDetails customUserDetails = new CustomUserDetails();
        customUserDetails.setEmail(retrieveAttr("email", oAuth2User));
        customUserDetails.setName(retrieveAttr("name", oAuth2User));
        customUserDetails.setAttributes(oAuth2User.getAttributes());
        customUserDetails.setAuthorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        return customUserDetails;
    }

    private String retrieveAttr(String attr, OAuth2User oAuth2User){
        Object attribute = oAuth2User.getAttributes().get(attr);
        return attribute== null ? "" : attribute.toString();
    }
}
