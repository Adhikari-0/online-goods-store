package com.store.organization;

public interface MembershipService {

    Membership addMember(Long userId, Long organizationId);

    Membership suspendMember(Long userId, Long organizationId);

    Membership reactivateMember(Long userId, Long organizationId);

    void removeMember(Long userId, Long organizationId);

    Membership getMembership(Long userId, Long organizationId);
}