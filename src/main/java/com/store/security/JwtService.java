package com.store.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey key;
    private final long accessTokenExpirationMs;
    private final long refreshTokenExpirationMs;
    private final String issuer;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.access-token-expiration-ms}") long accessTokenExpirationMs,
            @Value("${app.jwt.refresh-token-expiration-ms}") long refreshTokenExpirationMs,
            @Value("${app.jwt.issuer}") String issuer) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationMs = accessTokenExpirationMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
        this.issuer = issuer;
    }

    public String generateAccessToken(CustomUserDetails user) {
        return buildToken(user, accessTokenExpirationMs, "access");
    }

    public String generateRefreshToken(CustomUserDetails user) {
        return buildToken(user, refreshTokenExpirationMs, "refresh");
    }

    private String buildToken(CustomUserDetails user, long ttlMs, String type) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + ttlMs);

        return Jwts.builder()
            .issuer(issuer) // This is now the correct method on JwtBuilder
            .subject(user.getUsername())
            .claim("uid", user.getId())
            .claim("roles", user.getRoles())
            .claim("perms", user.getPermissions())
            .claim("type", type)
            .issuedAt(now)
            .expiration(exp)
            .signWith(key) // Use the SecretKey directly
            .compact();
    }

    // The key fix is here
    public Claims parse(String token) {
        return Jwts.parser() // Returns JwtParserBuilder
            .verifyWith(key) // Configure the builder
            .requireIssuer(issuer) // Validate the issuer
            .build() // Build the immutable parser
            .parseSignedClaims(token) // Parse
            .getPayload(); // Use getPayload() instead of getBody()
    }

    public boolean isValid(String token) {
        try {
            Claims c = parse(token);
            return c.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public String extractEmail(String token) {
        return parse(token).getSubject();
    }

    public Long extractUserId(String token) {
        return parse(token).get("uid", Long.class);
    }

    public String extractTokenType(String token) {
        return parse(token).get("type", String.class);
    }
}