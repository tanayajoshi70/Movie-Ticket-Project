package com.cdac.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

@Component
public class JWTUtils {
	
	@Value("${jwt.secret}")
	private String secret;
	 
	public String generateToken(String email) {
	    SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	    return Jwts.builder()
	            .setSubject(email)
	            .setIssuedAt(new Date())
	            .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 day
	            .signWith(key, SignatureAlgorithm.HS256)
	            .compact();
	}

	public String extractUsername(String token) {
	    SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	    return Jwts.parserBuilder()
	            .setSigningKey(key)
	            .build()
	            .parseClaimsJws(token)
	            .getBody()
	            .getSubject();
	}

    public boolean validateToken(String token, org.springframework.security.core.userdetails.UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername());
    }

	public static String getCurrentUserEmail() {
		 Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		    return auth.getName();
	}
}