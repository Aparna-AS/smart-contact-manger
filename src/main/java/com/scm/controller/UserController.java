package com.scm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.scm.entities.User;
import com.scm.helpers.Helper;
import com.scm.services.UserService;

@Controller
@RequestMapping("/user")
public class UserController {

    private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @RequestMapping("/dashboard")
    public String userDashboard() {
        return "user/dashboard";
    }

    @RequestMapping("/profile")
    public String userProfile(Authentication authentication, Model model) {
        String email = Helper.getEmailOfLoggedInUser(authentication);
        logger.info("Logged in user email: {}", email);

        User user = userService.getUserByEmail(email);
        model.addAttribute("LoggedInUser", user);
        return "user/profile";
    }
}
