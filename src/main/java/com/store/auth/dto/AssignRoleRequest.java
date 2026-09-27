package com.store.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AssignRoleRequest(
    @NotNull Long userId,
    @NotBlank String roleName,
    Long organizationId,   // null = global role
    String grantedBy
) {}