package com.scm.config;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.scm.entities.Providers;
import com.scm.entities.User;
import com.scm.helpers.AppConstants;
import com.scm.repositories.UserRepo;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuthAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(OAuthAuthenticationSuccessHandler.class);

    @Autowired
    private UserRepo userRepo;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        logger.info("Authentication successful for user: " + authentication.getName());

        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        String provider = token.getAuthorizedClientRegistrationId();
        logger.info("Provider: " + provider);

        DefaultOAuth2User oauthUser = (DefaultOAuth2User) authentication.getPrincipal();

        oauthUser.getAttributes().forEach((key, value) -> {
            logger.info(key + " : " + value);
        });

        String email = null;
        String name = null;
        String picture = null;
        String providerUserId = oauthUser.getName();

        // ✅ GOOGLE
        if (provider.equalsIgnoreCase("google")) {

            email = oauthUser.getAttribute("email");
            name = oauthUser.getAttribute("name");
            picture = oauthUser.getAttribute("picture");

        }

        // ✅ GITHUB
        else if (provider.equalsIgnoreCase("github")) {

            email = oauthUser.getAttribute("email");

            // 🔥 FIX: SAME fallback everywhere
            if (email == null) {
                String login = oauthUser.getAttribute("login");
                email = login + "@github.com";   // ✅ IMPORTANT FIX
            }

            name = oauthUser.getAttribute("name") != null
                    ? oauthUser.getAttribute("name")
                    : oauthUser.getAttribute("login");

            picture = oauthUser.getAttribute("avatar_url");
        }

        else {
            logger.info("Unknown provider");
        }

        // ✅ Check if user already exists
        User existingUser = userRepo.findByEmail(email).orElse(null);

        if (existingUser == null) {

            User user = new User();
            user.setUserId(UUID.randomUUID().toString());
            user.setEmail(email);
            user.setName(name);
            user.setProfilePic(picture);
            user.setProviderUserId(providerUserId);
            user.setProvider(provider.equalsIgnoreCase("google") ? Providers.GOOGLE : Providers.GITHUB);
            user.setPassword("dummy");
            user.setAbout("This account is created using " + provider + " login");
            user.setRoleList(List.of(AppConstants.ROLE_USER));
            user.setEnabled(true);
            user.setEmailVerified(true);

            userRepo.save(user);

            logger.info("User saved: " + email);
        }

        new DefaultRedirectStrategy().sendRedirect(request, response, "/user/profile");
    }
}