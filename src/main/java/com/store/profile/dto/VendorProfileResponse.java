package com.store.profile.dto;

import java.math.BigDecimal;

import com.store.profile.KycStatusEnum;

public record VendorProfileResponse(
	    Long id,
	    String businessName,
	    String taxId,
	    BigDecimal commissionPct,
	    String payoutAccount,
	    KycStatusEnum kycStatus
	) {}
