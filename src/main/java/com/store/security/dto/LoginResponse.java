package com.store.security.dto;

import java.util.Set;

public record LoginResponse(
    String accessToken,
    String refreshToken,
    String tokenType,
    long expiresInMs,
    UserSummary user
) {
    public record UserSummary(
        Long id,
        String email,
        String fullName,
        Set<String> roles,
        Set<String> permissions
    ) {}
}