package com.store.catalog.dto;

import com.store.catalog.Product;
import com.store.catalog.ProductStatusEnum;

import java.math.BigDecimal;

public record ProductSummaryResponse(
    Long id,
    String slug,
    String name,
    String shortDescription,
    Long vendorId,
    String categoryName,
    String brandName,
    ProductStatusEnum status,
    BigDecimal basePrice,
    BigDecimal compareAtPrice,
    String currency,
    boolean featured,
    BigDecimal averageRating,
    Integer reviewCount,
    String primaryImageUrl
) {
    public static ProductSummaryResponse from(Product p) {
        String primaryImage = p.getImages() == null ? null :
            p.getImages().stream()
                .filter(img -> img.isPrimary())
                .findFirst()
                .map(img -> img.getUrl())
                .orElse(
                    p.getImages().isEmpty() ? null : p.getImages().get(0).getUrl()
                );

        return new ProductSummaryResponse(
            p.getId(),
            p.getSlug(),
            p.getName(),
            p.getShortDescription(),
            p.getVendor() != null ? p.getVendor().getId() : null,
            p.getCategory() != null ? p.getCategory().getName() : null,
            p.getBrand() != null ? p.getBrand().getName() : null,
            p.getStatus(),
            p.getBasePrice(),
            p.getCompareAtPrice(),
            p.getCurrency(),
            p.isFeatured(),
            p.getAverageRating(),
            p.getReviewCount(),
            primaryImage
        );
    }
}