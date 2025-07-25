package com.dimsum.notenest20.controller;

import com.dimsum.notenest20.model.User;
import com.dimsum.notenest20.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
	
	private UserRepository userRepository;
	
	@Autowired
	public UserController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

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
    
    @PutMapping("/update")
    public ResponseEntity<?> updateUserProfile(@RequestBody Map<String, Object> updates, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        User user = (User) authentication.getPrincipal();

        // Update only allowed fields
        if (updates.containsKey("name")) {
            user.setName((String) updates.get("name"));
        }
        if (updates.containsKey("email")) {
            user.setEmail((String) updates.get("email"));
        }
        if (updates.containsKey("stream")) {
            user.setStream((String) updates.get("stream"));
        }
        if (updates.containsKey("university")) {
            user.setUniversity((String) updates.get("university"));
        }

        // Do NOT allow role changes
        // Do NOT update password here unless handled separately

        // Save changes to DB
        // Assuming you have a UserRepository
        userRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Profile updated successfully");

        return ResponseEntity.ok(response);
    }

}
