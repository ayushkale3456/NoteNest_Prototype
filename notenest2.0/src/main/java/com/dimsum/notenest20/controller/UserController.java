package com.dimsum.notenest20.controller;

import com.dimsum.notenest20.model.User;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        User userDetails = (User) authentication.getPrincipal();

        Map<String, Object> response = new HashMap<>();
        response.put("name", userDetails.getName());
        response.put("email", userDetails.getUsername());
        response.put("stream", userDetails.getStream());
        response.put("year", userDetails.getYear());
        response.put("university", userDetails.getUniversity());
        response.put("role", userDetails.getRole());

        return ResponseEntity.ok(response);
    }
}
