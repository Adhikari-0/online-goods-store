package com.store.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
    @NotBlank @Size(max = 150) String name,
    @Size(max = 100) String slug,
    String description,
    Long parentId,
    Integer sortOrder,
    Boolean active,
    @Size(max = 500) String imageUrl
) {}