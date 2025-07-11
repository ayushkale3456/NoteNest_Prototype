package com.dimsum.notenest20.security; // Recommended package

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

	@Value("${app.jwt.secret}") // Store this securely in application.properties/yml
	private String jwtSecret;

	@Value("${app.jwt.expiration-ms}") // Token expiration time in milliseconds
	private int jwtExpirationMs;

	private Key getSigningKey() {
		byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	public String generateToken(Authentication authentication) {
		// The principal is your UserDetails implementation (your User model)
		com.dimsum.notenest20.model.User userPrincipal = (com.dimsum.notenest20.model.User) authentication
				.getPrincipal();

		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

		return Jwts.builder().setSubject(userPrincipal.getEmail()) // Use email as subject
				.setIssuedAt(new Date()).setExpiration(expiryDate).signWith(getSigningKey(), SignatureAlgorithm.HS512)
				.compact();
	}

	public String getUsernameFromToken(String token) {
		return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody().getSubject();
	}

	public boolean validateToken(String authToken) {
		try {
			Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(authToken);
			return true;
		} catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
			System.out.println("Invalid JWT signature: " + e.getMessage());
		} catch (ExpiredJwtException e) {
			System.out.println("Expired JWT token: " + e.getMessage());
		} catch (UnsupportedJwtException e) {
			System.out.println("Unsupported JWT token: " + e.getMessage());
		} catch (IllegalArgumentException e) {
			System.out.println("JWT claims string is empty: " + e.getMessage());
		}
		return false;
	}
}