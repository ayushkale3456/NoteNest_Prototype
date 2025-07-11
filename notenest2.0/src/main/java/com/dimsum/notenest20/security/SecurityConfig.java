package com.dimsum.notenest20.security; // Recommended package for config

import com.dimsum.notenest20.repository.UserRepository;
import com.dimsum.notenest20.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer; // For csrf().disable() etc.
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration; // For CORS config
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.slf4j.Logger; // Import Logger
import org.slf4j.LoggerFactory; // Import LoggerFactory

import java.util.Arrays; // For CORS config

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class); // Add this logger

	private final UserRepository userRepository;
//	private final JwtAuthenticationFilter jwtAuthenticationFilter; // Inject your filter

	public SecurityConfig(UserRepository userRepository) {
		this.userRepository = userRepository;
//		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(); // Use BCrypt for password hashing
	}

	// Custom UserDetailsService to load user from DB
	@Bean
	public UserDetailsService userDetailsService() {
		return email -> {
			logger.info("Attempting to load user by email: {}", email); // Log email being searched
			return userRepository.findByEmail(email).map(user -> {
				logger.info("User found: {}", user.getEmail()); // Log user found
				logger.debug("User password from DB (hashed): {}", user.getPassword()); // Log stored hashed password
				return user;
			}).orElseThrow(() -> {
				logger.warn("User not found for email: {}", email); // Log user not found
				return new UsernameNotFoundException("User not found with email: " + email);
			});
		};
	}

	// Configure AuthenticationManager
	@Bean
	public AuthenticationManager authenticationManager(UserDetailsService userDetailsService,
			PasswordEncoder passwordEncoder) {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService);
		authenticationProvider.setPasswordEncoder(passwordEncoder);
		return new ProviderManager(authenticationProvider);
	}

	// Configure SecurityFilterChain for HTTP security rules
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(AbstractHttpConfigurer::disable) // Disable CSRF for stateless APIs (like REST with JWT)
				.cors(cors -> cors.configurationSource(request -> { // CORS configuration
					CorsConfiguration config = new CorsConfiguration();
					config.setAllowedOrigins(Arrays.asList("*")); // Allow all origins for development
					config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
					config.setAllowedHeaders(Arrays.asList("*")); // Allow all headers
					config.setAllowCredentials(true); // Allow credentials (e.g., for cookies/auth headers)
					return config;
				})).sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Use
																													// stateless
																													// sessions
																													// for
																													// JWT
				.authorizeHttpRequests(authorize -> authorize.requestMatchers("/api/auth/**").permitAll() // Allow
																											// access to
																											// auth
																											// endpoints
						.requestMatchers("/api/notes/all", "/api/notes/file/**").permitAll() // Allow public access to
																								// all notes/files
																								// (adjust as needed)
						.requestMatchers("/api/projects/all", "/api/projects/file/**").permitAll() // Allow public
																									// access to all
																									// projects/files
																									// (adjust as
																									// needed)
						.requestMatchers("/admin/**").hasRole("ADMIN") // Example: only ADMIN can access /admin
						.anyRequest().authenticated() // All other requests must be authenticated
				)
				// Add JWT filter here (after you implement it)
				// .addFilterBefore(jwtRequestFilter,
				// UsernamePasswordAuthenticationFilter.class)
				// ADD THE JWT FILTER HERE
				.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	// Optional: If you need a global CORS filter (useful for preflight requests)
	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.addAllowedOrigin("*");
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");
		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}

	// You need to explicitly define JwtAuthenticationFilter as a bean for it to be
	// injected correctly
	// or rely on @Component scanning.
	// However, if you need to use it with addFilterBefore(), making it a @Bean is
	// cleaner.
	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() {
		// Spring will automatically autowire JwtTokenProvider and UserDetailsService
		// into its constructor
		// because they are also beans/@Components.
		return new JwtAuthenticationFilter();
	}
}