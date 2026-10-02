package com.store.catalog.dto;

import com.store.catalog.Product;
import com.store.catalog.ProductStatusEnum;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record ProductResponse(
    Long id,
    String slug,
    String name,
    String description,
    String shortDescription,
    Long vendorId,
    Long categoryId,
    String categoryName,
    Long brandId,
    String brandName,
    ProductStatusEnum status,
    boolean hasVariants,
    BigDecimal basePrice,
    BigDecimal compareAtPrice,
    String currency,
    boolean featured,
    BigDecimal averageRating,
    Integer reviewCount,
    Long totalSold,
    List<ProductImageResponse> images,
    List<ProductVariantResponse> variants,
    Instant createdAt,
    Instant updatedAt
) {
    public static ProductResponse from(Product p) {
        return new ProductResponse(
            p.getId(),
            p.getSlug(),
            p.getName(),
            p.getDescription(),
            p.getShortDescription(),
            p.getVendor() != null ? p.getVendor().getId() : null,
            p.getCategory() != null ? p.getCategory().getId() : null,
            p.getCategory() != null ? p.getCategory().getName() : null,
            p.getBrand() != null ? p.getBrand().getId() : null,
            p.getBrand() != null ? p.getBrand().getName() : null,
            p.getStatus(),
            p.isHasVariants(),
            p.getBasePrice(),
            p.getCompareAtPrice(),
            p.getCurrency(),
            p.isFeatured(),
            p.getAverageRating(),
            p.getReviewCount(),
            p.getTotalSold(),
            p.getImages() != null
                ? p.getImages().stream().map(ProductImageResponse::from).toList()
                : List.of(),
            p.getVariants() != null
                ? p.getVariants().stream().map(ProductVariantResponse::from).toList()
                : List.of(),
            p.getCreatedAt(),
            p.getUpdatedAt()
        );
    }
}