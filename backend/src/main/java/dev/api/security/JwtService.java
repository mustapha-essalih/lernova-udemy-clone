package dev.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private SecretKey secretKey;

    public JwtService() {
        secretKey = Jwts.SIG.HS256.key().build(); // automatique genrated
    }

    @Value("${token.expirationms}")
    private long jwtExpiration;

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>(); 
        claims.put("sub", username);
        claims.put("iat", System.currentTimeMillis());
        claims.put("exp", System.currentTimeMillis() + jwtExpiration);
        return Jwts.builder()
                .claims(claims)
                .signWith(secretKey)
                .compact();
    }

    public boolean isTokenValid(String token, String username) {
        String userName = extractUsername(token);
        return (userName.equals(username)) && !isTokenExpired(token);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
 
}
