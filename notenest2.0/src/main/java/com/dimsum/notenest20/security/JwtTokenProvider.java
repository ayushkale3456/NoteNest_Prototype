//package com.dimsum.notenest20.security;
//
//import io.jsonwebtoken.*;
//import io.jsonwebtoken.io.Decoders;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.Authentication;
//import org.springframework.stereotype.Component;
//
//import java.security.Key;
//import java.util.Date;
//
//@Component
//public class JwtTokenProvider {
//
//	@Value("${app.jwt.secret}")
//	private String jwtSecret;
//
//	@Value("${app.jwt.access-expiration-ms}")
//	private long accessTokenExpirationMs;
//
//	@Value("${app.jwt.refresh-expiration-ms}")
//	private long refreshTokenExpirationMs;
//
//	private Key getSigningKey() {
//		byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
//		return Keys.hmacShaKeyFor(keyBytes);
//	}
//
//	public String generateAccessToken(Authentication authentication) {
//		com.dimsum.notenest20.model.User userPrincipal = (com.dimsum.notenest20.model.User) authentication
//				.getPrincipal();
//		return buildToken(userPrincipal.getEmail(), accessTokenExpirationMs);
//	}
//
//	public String generateRefreshToken(Authentication authentication) {
//		com.dimsum.notenest20.model.User userPrincipal = (com.dimsum.notenest20.model.User) authentication
//				.getPrincipal();
//		return buildToken(userPrincipal.getEmail(), refreshTokenExpirationMs);
//	}
//
//	public String buildToken(String email, long expirationMs) {
//		Date now = new Date();
//		Date expiryDate = new Date(now.getTime() + expirationMs);
//
//		return Jwts.builder().setSubject(email).setIssuedAt(now).setExpiration(expiryDate)
//				.signWith(getSigningKey(), SignatureAlgorithm.HS512).compact();
//	}
//
//	public String getUsernameFromToken(String token) {
//		return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody().getSubject();
//	}
//
//	public boolean validateToken(String token) {
//		try {
//			Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
//			return true;
//		} catch (JwtException | IllegalArgumentException e) {
//			System.out.println("Invalid JWT token: " + e.getMessage());
//			return false;
//		}
//	}
//}





package com.dimsum.notenest20.security;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.dimsum.notenest20.model.User;

import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.access-expiration-ms}")
    private long accessTokenExpirationMs;

    @Value("${app.jwt.refresh-expiration-ms}")
    private long refreshTokenExpirationMs;

    // Generate Access Token
    public String generateAccessToken(Authentication authentication) {
        User userPrincipal = (User) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    // Generate Refresh Token
    public String generateRefreshToken(Authentication authentication) {
        User userPrincipal = (User) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    // Generate Access Token from Email
    public String generateAccessTokenFromEmail(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getEmailFromToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }
}
