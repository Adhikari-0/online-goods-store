package com.store.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BrandRequest(
    @NotBlank @Size(max = 150) String name,
    @Size(max = 100) String slug,
    String description,
    @Size(max = 500) String logoUrl,
    Boolean active
) {}