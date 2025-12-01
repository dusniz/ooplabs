package ru.ssau.tk.enjoyers.ooplabs.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import ru.ssau.tk.enjoyers.ooplabs.Role;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JWTUtil {

    private static final String SECRET_KEY = "507629b3f402a8bd8ea0991ed7fd944bfa7e50e9e7cbf937ca32bc207a45368e";
    private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 hour
    private static final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    public static String generateToken(String username, Role role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public static Claims validateToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token expired");
        } catch (JwtException e) {
            throw new RuntimeException("Invalid token");
        }
    }

    public static String getUsernameFromToken(String token) {
        Claims claims = validateToken(token);
        return claims.getSubject();
    }

    public static String getRoleFromToken(String token) {
        Claims claims = validateToken(token);
        return (String) claims.get("role");
    }
}