// profile/dto/DeliveryAgentProfileRequest.java
package com.store.profile.dto;

public record DeliveryAgentProfileRequest(
    String licenseNumber,
    String vehicleType,
    String vehicleNumber
) {}