package com.store.auth.dto;

import com.store.auth.Permission;

public record PermissionResponse(
    Long id,
    String code,
    String description
) {
    public static PermissionResponse from(Permission permission) {
        return new PermissionResponse(
            permission.getId(),
            permission.getCode(),
            permission.getDescription()
        );
    }
}