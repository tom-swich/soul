package com.solvtrends.monolito.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.text.ParseException;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {
    private final long EXPIRATION_TIME = 86400000; // 1 dia

    @Value("${jwt.secret}")
    private String SECRET;

    public String generateToken(String username) {
        SecretKey key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET));

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }
    public String getUsernameFromToken(String token) throws ParseException {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public boolean validateToken(String token) throws Exception {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(SECRET)
                    .build()
                    .parseClaimsJws(token);

            return true;
        } catch (JwtException | IllegalArgumentException e) {
            throw new SecurityException("Token inválido ou expirado", e);
        }
    }
}
