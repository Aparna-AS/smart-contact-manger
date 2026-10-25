package com.scm.helpers;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class Helper {

    public static String getEmailOfLoggedInUser(Authentication authentication) {
        if (authentication instanceof OAuth2AuthenticationToken token) {
            OAuth2User oauthUser = token.getPrincipal();
            String email = oauthUser.getAttribute("email");

            if (email == null && "github".equalsIgnoreCase(token.getAuthorizedClientRegistrationId())) {
                String login = oauthUser.getAttribute("login");
                if (login != null) {
                    email = login + "@github.com";
                }
            }

            return email;
        }

        return authentication.getName();
    }
}
