package me.rhythmvarshney.blogapplication.rest;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserRestController {

    @Autowired
    private UserDetailsService userDetailsService;

    @PostMapping("/authenticate")
    @ExceptionHandler
    public ResponseEntity<String> authenticate(@RequestBody JsonParser jsonParser) {
        // You can create a custom AuthenticationRequest class to hold username and password.

        // Load the user details (including the encoded password) from the UserDetailsService.
        UserDetails userDetails = userDetailsService.loadUserByUsername(jsonParser.getEmail());
        System.out.println(userDetails + " User details here");

        // Check if the provided password matches the encoded password in the user details.
        if (new BCryptPasswordEncoder().matches(jsonParser.getPassword(), userDetails.getPassword())) {
            // If the passwords match, the user is valid.
            // You can return a success message or generate a token for the user.
            return ResponseEntity.ok("Authentication successful");
        } else {
            // If the passwords do not match, the user is not valid.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
        }
    }
}
