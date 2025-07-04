package com.dimsum.notenest20.controller;

import com.dimsum.notenest20.model.Role;
import com.dimsum.notenest20.model.User;
import com.dimsum.notenest20.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

	private UserRepository userRepository;

	@Autowired
	public AuthController(UserRepository userRepository) {
		this.userRepository = userRepository;
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
			return ResponseEntity.badRequest().body("Email already exists");
		}

		Role role = Role.valueOf(roleStr.toUpperCase());

		User user = new User();
		user.setName(name);
		user.setEmail(email);
		user.setPassword(password);
		user.setStream(stream);
		user.setYear(year);
		user.setRole(role);

		userRepository.save(user);
		return ResponseEntity.ok("User registered successfully");
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
		String email = body.get("email");
		String password = body.get("password");

		return userRepository.findByEmail(email).map(user -> {
			if (user.getPassword().equals(password)) {
				return ResponseEntity.ok(Map.of("name", user.getName(), "email", user.getEmail(), "stream",
						user.getStream(), "year", user.getYear(), "role", user.getRole().name()));
			} else {
				return ResponseEntity.status(401).body("Invalid password");
			}
		}).orElse(ResponseEntity.status(404).body("User not found"));
	}
}
