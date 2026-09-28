package com.store.organization.dto;

import com.store.organization.Organization;
import com.store.organization.OrganizationStatusEnum;

import java.time.Instant;

public record OrganizationResponse(
    Long id,
    String slug,
    String name,
    OrganizationStatusEnum status,
    Instant createdAt
) {
    public static OrganizationResponse from(Organization org) {
        return new OrganizationResponse(
            org.getId(),
            org.getSlug(),
            org.getName(),
            org.getStatus(),
            org.getCreatedAt()
        );
    }
}