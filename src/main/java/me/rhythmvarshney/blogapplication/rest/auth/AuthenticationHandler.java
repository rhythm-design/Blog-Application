package me.rhythmvarshney.blogapplication.rest.auth;

import me.rhythmvarshney.blogapplication.rest.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Component
public class AuthenticationHandler {

    @Autowired
    private UserDetailsService userDetailsService;

    public boolean authenticate(String email, String password) {

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        System.out.println(userDetails + " User details here");

        if (new BCryptPasswordEncoder().matches(password, userDetails.getPassword())) {
            return true;
        } else {
            return false;
        }
    }

}
