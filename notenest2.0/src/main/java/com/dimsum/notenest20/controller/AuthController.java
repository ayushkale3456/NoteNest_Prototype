package com.dimsum.notenest20.controller;

import com.dimsum.notenest20.model.Role;
import com.dimsum.notenest20.model.User;
import com.dimsum.notenest20.repository.UserRepository;
import com.dimsum.notenest20.security.JwtTokenProvider;
import org.slf4j.Logger; // Import Logger
import org.slf4j.LoggerFactory; // Import LoggerFactory

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
	
	private static final Logger logger = LoggerFactory.getLogger(AuthController.class); // Add this logger

	private UserRepository userRepository;
	private PasswordEncoder passwordEncoder;
	private AuthenticationManager authenticationManager; // To authenticate logins
	private JwtTokenProvider tokenProvider; // To generate JWTs

	@Autowired
	public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder,
			AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
		this.tokenProvider = tokenProvider;

	}

	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
		String name = body.get("name");
		String email = body.get("email");
		String password = body.get("password");
		String stream = body.get("stream");
		String year = body.get("year");
		String roleStr = body.get("role");

		if (userRepository.existsByEmail(email)) {
			// Return structured JSON for error
			return ResponseEntity.badRequest().body(Map.of("message", "Email already exists"));
		}

		// Handle potential invalid role string
		Role role;
		try {
			role = Role.valueOf(roleStr.toUpperCase());
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(Map.of("message", "Invalid role specified"));
		}

		User user = new User();
		user.setName(name);
		user.setEmail(email);
//		user.setPassword(password);
		user.setPassword(passwordEncoder.encode(password));
		user.setStream(stream);
		user.setYear(year);
		user.setRole(role);

		userRepository.save(user);
		return ResponseEntity.ok(Map.of("message", "User registered successfully"));
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
		String email = body.get("email");
		String password = body.get("password");

//		return userRepository.findByEmail(email).map(user -> {
//			if (user.getPassword().equals(password)) {
//				return ResponseEntity.ok(Map.of("name", user.getName(), "email", user.getEmail(), "stream",
//						user.getStream(), "year", user.getYear(), "role", user.getRole().name()));
//			} else {
//				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid password"));
//			}
//		}).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found")));

		
		logger.info("Login attempt for email: {}", email); // Log the incoming email
        logger.debug("Password received (for debugging ONLY): {}", password); // CAUTION: Logging plaintext password is BAD practice in production! Remove later.
		
		try {
			Authentication authentication = authenticationManager.authenticate( // <--- AUTHENTICATE
					new UsernamePasswordAuthenticationToken(email, password));

			SecurityContextHolder.getContext().setAuthentication(authentication); // Set in context

			String jwt = tokenProvider.generateToken(authentication); // <--- GENERATE JWT

			// Retrieve the authenticated user principal to get other details
			User userPrincipal = (User) authentication.getPrincipal();
			logger.info("User {} successfully authenticated.", userPrincipal.getEmail()); // Log success

			// Return the token AND user details
			return ResponseEntity.ok(Map.of("token", jwt, // <--- RETURN THE TOKEN
					"name", userPrincipal.getName(), "email", userPrincipal.getEmail(), "stream",
					userPrincipal.getStream(), "year", userPrincipal.getYear(), "role",
					userPrincipal.getRole().name()));
		} catch (Exception e) {
			
			// Catch AuthenticationException (e.g., BadCredentialsException, UsernameNotFoundException)
            logger.error("Authentication failed for email {}: {}", email, e.getMessage()); // Log the failure and reason
			// Catch AuthenticationException (e.g., BadCredentialsException,
			// UsernameNotFoundException)
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid credentials"));
		}

	}

	@GetMapping("/me")
	public ResponseEntity<?> getLoggedInUserDetails(@AuthenticationPrincipal User user) {
//		return ResponseEntity.ok(user); // Return name, email, role, etc.
		
		// This will now work IF a valid JWT is provided in the Authorization header
        // and the JWT filter (next step) is correctly configured.
        if (user == null) { // Should ideally not happen if properly authenticated, but good for debugging
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "No authenticated user found."));
        }
        return ResponseEntity.ok(Map.of(
            "name", user.getName(),
            "email", user.getEmail(),
            "stream", user.getStream(), // Assuming User class has getStream() etc.
            "year", user.getYear(),
            "role", user.getRole().name()
        ));
	}

}
