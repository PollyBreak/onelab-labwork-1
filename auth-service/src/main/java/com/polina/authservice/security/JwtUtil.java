package com.polina.authservice.security;

import com.polina.authservice.dto.TokenValidationResponse;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    private final String secret = "verysecretkeyilove8dnd888games99weareelves3333";
    private final long jwtExpiration = 86400000;

    public String generateToken(Long userId, String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }


    public String extractUsername(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    public TokenValidationResponse validateToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return new TokenValidationResponse(false, null, null, null);
        }
        token = token.substring(7);
        try {
            Claims claims = extractClaims(token);
            Long userId = claims.get("userId", Long.class);
            String username = claims.getSubject();
            String role = claims.get("role", String.class);
            return new TokenValidationResponse(true, userId, username, role);
        } catch (Exception e) {
            return new TokenValidationResponse(false, null, null, null);
        }
    }
}
