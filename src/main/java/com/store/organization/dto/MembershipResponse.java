package com.store.organization.dto;

import com.store.organization.Membership;
import com.store.organization.MembershipStateEnum;

import java.time.Instant;

public record MembershipResponse(
    Long id,
    Long userId,
    String userEmail,
    Long organizationId,
    String organizationSlug,
    MembershipStateEnum state,
    Instant joinedAt,
    Instant leftAt
) {
    public static MembershipResponse from(Membership m) {
        return new MembershipResponse(
            m.getId(),
            m.getUser() != null ? m.getUser().getId() : null,
            m.getUser() != null ? m.getUser().getEmail() : null,
            m.getOrganization() != null ? m.getOrganization().getId() : null,
            m.getOrganization() != null ? m.getOrganization().getSlug() : null,
            m.getState(),
            m.getJoinedAt(),
            m.getLeftAt()
        );
    }
}