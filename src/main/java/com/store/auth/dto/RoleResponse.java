package com.store.auth.dto;

import java.util.Set;

public record RoleResponse(
    Long id,
    String name,
    String description,
    boolean system,
    Set<String> permissions
) {}