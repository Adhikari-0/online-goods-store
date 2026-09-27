package com.store.user.dto;

import com.store.user.UserStatusEnum;
import java.time.Instant;
import java.util.Set;

public record UserResponse(
    Long id,
    String email,
    String fullName,
    String phone,
    String avatarUrl,
    UserStatusEnum status,
    boolean emailVerified,
    Set<String> roles,
    Instant createdAt,
    Instant lastLoginAt
) {}