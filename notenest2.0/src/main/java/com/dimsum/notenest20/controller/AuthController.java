package com.dimsum.notenest20.controller;

import com.dimsum.notenest20.model.AuthResponse;
import com.dimsum.notenest20.model.Role;
import com.dimsum.notenest20.model.User;
import com.dimsum.notenest20.repository.UserRepository;
import com.dimsum.notenest20.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

	private UserRepository userRepository;
	private PasswordEncoder passwordEncoder;
	private AuthenticationManager authenticationManager;
	private JwtTokenProvider tokenProvider;

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
		String university = body.get("university");

		if (userRepository.existsByEmail(email)) {
			return ResponseEntity.badRequest().body(Map.of("message", "Email already exists"));
		}

		Role role;
		try {
			role = Role.valueOf(roleStr.toUpperCase());
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(Map.of("message", "Invalid role specified"));
		}

		User user = new User();
		user.setName(name);
		user.setEmail(email);
		user.setPassword(passwordEncoder.encode(password));
		user.setStream(stream);
		user.setYear(year);
		user.setRole(role);
		user.setUniversity(university);

		userRepository.save(user);
		return ResponseEntity.ok(Map.of("message", "User registered successfully"));
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
		String email = body.get("email");
		String password = body.get("password");

		try {
			Authentication authentication = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(email, password));

			SecurityContextHolder.getContext().setAuthentication(authentication);

			String accessToken = tokenProvider.generateAccessToken(authentication);
			String refreshToken = tokenProvider.generateRefreshToken(authentication);

			User user = userRepository.findByEmail(email).orElseThrow();
			return ResponseEntity
					.ok(new AuthResponse(accessToken, refreshToken, user.getRole().name(), user.getEmail(), user.getStream()));

		} catch (Exception e) {
			logger.error("Login process failed for email: {}. Error: {}", email, e.getMessage());
			logger.error("Stack trace:", e);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid credentials"));
		}
	}

	@PostMapping("/refresh-token")
	public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> body) {
		String refreshToken = body.get("refreshToken");

		if (tokenProvider.validateToken(refreshToken)) {
			String email = tokenProvider.getEmailFromToken(refreshToken);
			String newAccessToken = tokenProvider.generateAccessTokenFromEmail(email);

			return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of("message", "Invalid or expired refresh token"));
		}
	}

	@GetMapping("/me")
	public ResponseEntity<?> getLoggedInUserDetails(@AuthenticationPrincipal User user) {
		if (user == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of("message", "No authenticated user found."));
		}
		return ResponseEntity.ok(Map.of("name", user.getName(), "email", user.getEmail(), "stream", user.getStream(),
				"year", user.getYear(), "role", user.getRole().name()));
	}
}