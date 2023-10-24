package me.rhythmvarshney.blogapplication.controller;

import me.rhythmvarshney.blogapplication.entity.User;
import me.rhythmvarshney.blogapplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthenticationController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String loginPage(){
        return "login-page";
    }

    @GetMapping("/register")
    public String register(){
        return "signup-page";
    }

    @GetMapping("/success")
    public String saveUserToDB(@RequestParam(value = "username", required = false) String username,
                               @RequestParam(value = "email", required = false) String email,
                               @RequestParam(value = "password", required = false) String password,
                               @RequestParam(value = "confirmPassword", required = false) String confirmPassword){
        User user = new User();
        user.setName(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole("NORMAL");
        userService.saveUser(user);

        return "redirect:/login";
    }

}
