package com.store.catalog.dto;

import com.store.catalog.Brand;

import java.time.Instant;

public record BrandResponse(
    Long id,
    String slug,
    String name,
    String description,
    String logoUrl,
    boolean active,
    Instant createdAt
) {
    public static BrandResponse from(Brand b) {
        return new BrandResponse(
            b.getId(),
            b.getSlug(),
            b.getName(),
            b.getDescription(),
            b.getLogoUrl(),
            b.isActive(),
            b.getCreatedAt()
        );
    }
}