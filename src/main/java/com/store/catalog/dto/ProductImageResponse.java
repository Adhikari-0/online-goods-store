package com.store.catalog.dto;

import com.store.catalog.ProductImage;

public record ProductImageResponse(
    Long id,
    String url,
    String alt,
    Integer sortOrder,
    boolean primary
) {
    public static ProductImageResponse from(ProductImage img) {
        return new ProductImageResponse(
            img.getId(),
            img.getUrl(),
            img.getAlt(),
            img.getSortOrder(),
            img.isPrimary()
        );
    }
}