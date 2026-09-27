package com.store.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	// --- Basic lookups ---

	Optional<User> findByEmail(String email);

	boolean existsByEmail(String email);

	Optional<User> findByPhone(String phone);

	// --- Status-based queries ---

	Page<User> findAllByStatus(UserStatusEnum status, Pageable pageable);

	List<User> findAllByStatus(UserStatusEnum status);

	long countByStatus(UserStatusEnum status);

	// --- Fetch with roles (avoids N+1) ---

	@Query("""
			    SELECT DISTINCT u FROM User u
			    LEFT JOIN FETCH u.userRoles ur
			    LEFT JOIN FETCH ur.role
			    WHERE u.email = :email
			""")
	Optional<User> findByEmailWithRoles(@Param("email") String email);

	@Query("""
			    SELECT DISTINCT u FROM User u
			    LEFT JOIN FETCH u.userRoles ur
			    LEFT JOIN FETCH ur.role
			    WHERE u.id = :id
			""")
	Optional<User> findByIdWithRoles(@Param("id") Long id);

	// --- Search (email or name) ---

	@Query("""
			    SELECT u FROM User u
			    WHERE LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
			       OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
			""")
	Page<User> search(@Param("keyword") String keyword, Pageable pageable);

	// --- Users by role ---

	@Query("""
			    SELECT DISTINCT u FROM User u
			    JOIN u.userRoles ur
			    JOIN ur.role r
			    WHERE r.name = :roleName
			""")
	List<User> findAllByRoleName(@Param("roleName") String roleName);

	// --- Users in organization ---

	@Query("""
			    SELECT DISTINCT u FROM User u
			    JOIN u.memberships m
			    WHERE m.organization.id = :orgId
			      AND m.state = 'ACTIVE'
			""")
	List<User> findAllActiveMembersOfOrganization(@Param("orgId") Long orgId);

	// --- Bulk status update ---

	@Modifying
	@Query("UPDATE User u SET u.status = :status WHERE u.id IN :ids")
	int updateStatusByIds(@Param("ids") List<Long> ids, @Param("status") UserStatusEnum status);

	// --- Login tracking ---

	@Modifying
	@Query("UPDATE User u SET u.lastLoginAt = :time WHERE u.id = :id")
	int updateLastLogin(@Param("id") Long id, @Param("time") Instant time);

	// --- Verification ---

	@Modifying
	@Query("UPDATE User u SET u.emailVerified = true WHERE u.id = :id")
	int markEmailVerified(@Param("id") Long id);

	// --- Cleanup / maintenance ---

	@Query("""
			    SELECT u FROM User u
			    WHERE u.status = 'PENDING_VERIFICATION'
			      AND u.createdAt < :cutoff
			""")
	List<User> findStalePendingUsers(@Param("cutoff") Instant cutoff);
}