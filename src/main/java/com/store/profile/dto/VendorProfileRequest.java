package com.store.profile.dto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public record VendorProfileRequest(
    @NotBlank String businessName,
    String taxId,
    BigDecimal commissionPct,
    String payoutAccount
) {}