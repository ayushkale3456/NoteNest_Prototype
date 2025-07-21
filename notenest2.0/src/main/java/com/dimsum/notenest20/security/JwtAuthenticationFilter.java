package com.dimsum.notenest20.security; // Recommended package

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService; // Your custom UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

//--- CRITICAL: ADD THESE SLF4J IMPORTS ---
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//--- END CRITICAL ADDITION ---

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	
	// --- CRITICAL: INSTANTIATE THE LOGGER CORRECTLY ---
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    // --- END CRITICAL ADDITION ---

	@Autowired
	private JwtTokenProvider tokenProvider;

	@Autowired
	private UserDetailsService userDetailsService; // Your UserDetailsService bean

//	@Override
//	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//			throws ServletException, IOException {
//		try {
//			String jwt = getJwtFromRequest(request);
//
//			if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
//				String username = tokenProvider.getEmailFromToken(jwt); // This is the email
//
//				UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
//						userDetails, null, userDetails.getAuthorities());
//				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//
//				SecurityContextHolder.getContext().setAuthentication(authentication);
//			}
//		} catch (Exception ex) {
//			logger.error("Could not set user authentication in security context", ex);
//		}
//
//		filterChain.doFilter(request, response);
//	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
	        throws ServletException, IOException {
	    try {
	        logger.debug("Processing request to: {}", request.getRequestURI()); // NEW LOG
	        String jwt = getJwtFromRequest(request);
	        logger.debug("Extracted JWT: {}", jwt != null ? "Token found" : "No token found"); // NEW LOG

	        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
	            logger.debug("JWT is valid."); // NEW LOG
	            String username = tokenProvider.getEmailFromToken(jwt);
	            logger.debug("Email from token: {}", username); // NEW LOG

	            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
	            logger.debug("User details loaded for: {}", userDetails.getUsername()); // NEW LOG

	            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
	                    userDetails, null, userDetails.getAuthorities());
	            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

	            SecurityContextHolder.getContext().setAuthentication(authentication);
	            logger.debug("SecurityContextHolder updated with authentication for user: {}", userDetails.getUsername()); // NEW LOG
	        } else {
	            logger.debug("JWT is either empty or invalid. Request will proceed without authentication."); // NEW LOG
	        }
	    } catch (Exception ex) {
	        logger.error("Could not set user authentication in security context for request: {}", request.getRequestURI(), ex); // Modified log for clarity
	    }

	    filterChain.doFilter(request, response);
	}

	private String getJwtFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}
}