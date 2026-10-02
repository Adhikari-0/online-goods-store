package com.store.catalog.dto;

import com.store.catalog.ProductVariant;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductVariantResponse(
    Long id,
    String sku,
    String name,
    BigDecimal price,
    BigDecimal compareAtPrice,
    BigDecimal costPrice,
    Integer weightGrams,
    String barcode,
    String imageUrl,
    boolean active,
    Instant createdAt
) {
    public static ProductVariantResponse from(ProductVariant v) {
        return new ProductVariantResponse(
            v.getId(),
            v.getSku(),
            v.getName(),
            v.getPrice(),
            v.getCompareAtPrice(),
            v.getCostPrice(),
            v.getWeightGrams(),
            v.getBarcode(),
            v.getImageUrl(),
            v.isActive(),
            v.getCreatedAt()
        );
    }
}