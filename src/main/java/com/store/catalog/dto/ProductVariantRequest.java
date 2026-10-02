package com.store.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record ProductVariantRequest(
    @NotBlank String sku,
    String name,
    @NotNull @PositiveOrZero BigDecimal price,
    @PositiveOrZero BigDecimal compareAtPrice,
    @PositiveOrZero BigDecimal costPrice,
    @PositiveOrZero Integer weightGrams,
    String barcode,
    String imageUrl,
    Boolean active
) {}