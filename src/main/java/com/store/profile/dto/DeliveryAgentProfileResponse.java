package com.store.profile.dto;

import java.time.Instant;

import com.store.profile.AgentStatusEnum;

public record DeliveryAgentProfileResponse(
	    Long id,
	    String licenseNumber,
	    String vehicleType,
	    String vehicleNumber,
	    boolean available,
	    Double currentLatitude,
	    Double currentLongitude,
	    Instant lastLocationAt,
	    AgentStatusEnum status
	) {}
