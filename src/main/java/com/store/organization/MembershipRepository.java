package com.store.organization;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, Long> {

    // --- Basic lookups ---

    Optional<Membership> findByUserIdAndOrganizationId(Long userId, Long organizationId);

    boolean existsByUserIdAndOrganizationId(Long userId, Long organizationId);

    boolean existsByUserIdAndOrganizationIdAndState(
        Long userId, Long organizationId, MembershipStateEnum state);

    List<Membership> findAllByUserId(Long userId);

    List<Membership> findAllByOrganizationId(Long organizationId);

    // --- Filtered by state ---

    List<Membership> findAllByOrganizationIdAndState(
        Long organizationId, MembershipStateEnum state);

    List<Membership> findAllByUserIdAndState(Long userId, MembershipStateEnum state);

    Page<Membership> findAllByOrganizationIdAndState(
        Long organizationId, MembershipStateEnum state, Pageable pageable);

    // --- Fetch with user + organization (avoids N+1) ---

    @Query("""
        SELECT DISTINCT m FROM Membership m
        JOIN FETCH m.user
        JOIN FETCH m.organization
        WHERE m.organization.id = :orgId
    """)
    List<Membership> findAllByOrganizationIdWithDetails(@Param("orgId") Long orgId);

    // --- Count ---

    long countByOrganizationIdAndState(Long organizationId, MembershipStateEnum state);

    // --- Bulk operations ---

    @Modifying
    @Query("""
        UPDATE Membership m
        SET m.state = :state, m.leftAt = CURRENT_TIMESTAMP
        WHERE m.organization.id = :orgId
          AND m.state = 'ACTIVE'
    """)
    int removeAllMembersFromOrganization(@Param("orgId") Long orgId,
                                         @Param("state") MembershipStateEnum state);

    @Modifying
    void deleteAllByUserId(Long userId);

    @Modifying
    void deleteAllByOrganizationId(Long organizationId);

    // --- Find users with role in org (joins through UserRole) ---

    @Query("""
        SELECT DISTINCT m.user FROM Membership m
        JOIN UserRole ur ON ur.user = m.user
        WHERE m.organization.id = :orgId
          AND m.state = 'ACTIVE'
          AND ur.role.name = :roleName
          AND ur.organization.id = :orgId
    """)
    List<com.store.user.User> findMembersWithRole(
        @Param("orgId") Long orgId,
        @Param("roleName") String roleName);
}