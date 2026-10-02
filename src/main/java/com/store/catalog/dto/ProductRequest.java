package com.store.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public record ProductRequest(
    @NotBlank @Size(max = 255) String name,
    @Size(max = 150) String slug,
    String description,
    String shortDescription,
    Long categoryId,
    Long brandId,
    @PositiveOrZero BigDecimal basePrice,
    @PositiveOrZero BigDecimal compareAtPrice,
    String currency,
    Boolean featured,
    Boolean hasVariants,
    List<ProductImageRequest> images,
    List<ProductVariantRequest> variants
) {}