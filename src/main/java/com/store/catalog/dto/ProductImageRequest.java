package com.store.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProductImageRequest(
    @NotBlank @Size(max = 500) String url,
    @Size(max = 255) String alt,
    Integer sortOrder,
    Boolean primary
) {}