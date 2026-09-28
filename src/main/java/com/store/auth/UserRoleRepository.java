package com.store.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

	// Basic lookups

	List<UserRole> findAllByUserId(Long userId);

	List<UserRole> findAllByRoleId(Long roleId);

	List<UserRole> findAllByOrganizationId(Long organizationId);

	Optional<UserRole> findByUserIdAndRoleIdAndOrganizationId(Long userId, Long roleId, Long organizationId);

	boolean existsByUserIdAndRoleIdAndOrganizationId(Long userId, Long roleId, Long organizationId);

	// Fetch with role + permissions (avoids N+1)

	@Query("""
			    SELECT DISTINCT ur FROM UserRole ur
			    JOIN FETCH ur.role r
			    LEFT JOIN FETCH r.permissions
			    WHERE ur.user.id = :userId
			""")
	List<UserRole> findAllByUserIdWithRoleAndPermissions(@Param("userId") Long userId);

	// Delete

	@Modifying
	@Query("""
			    DELETE FROM UserRole ur
			    WHERE ur.user.id = :userId
			      AND ur.role.id = :roleId
			      AND ur.organization.id = :orgId
			""")
	int deleteByUserIdAndRoleIdAndOrganizationId(@Param("userId") Long userId, @Param("roleId") Long roleId,
			@Param("orgId") Long orgId);

	@Modifying
	void deleteAllByUserId(Long userId);

	@Modifying
	void deleteAllByRoleId(Long roleId);

	// Role checks (fast path)

	@Query("""
			    SELECT COUNT(ur) > 0 FROM UserRole ur
			    WHERE ur.user.id = :userId
			      AND ur.role.name = :roleName
			""")
	boolean userHasRole(@Param("userId") Long userId, @Param("roleName") String roleName);

	// --- Expired roles cleanup ---

	@Query("""
			    SELECT ur FROM UserRole ur
			    WHERE ur.expiresAt IS NOT NULL
			      AND ur.expiresAt < :now
			""")
	List<UserRole> findExpiredRoles(@Param("now") Instant now);

	@Modifying
	void deleteAllByExpiresAtBefore(Instant now);

	// --- Count users per role ---

	@Query("SELECT COUNT(ur) FROM UserRole ur WHERE ur.role.id = :roleId")
	long countByRoleId(@Param("roleId") Long roleId);

	// --- Users with a specific role in an org

	@Query("""
			    SELECT DISTINCT ur.user FROM UserRole ur
			    WHERE ur.role.name = :roleName
			      AND ur.organization.id = :orgId
			""")
	List<com.store.user.User> findUsersByRoleAndOrganization(@Param("roleName") String roleName,
			@Param("orgId") Long orgId);
}