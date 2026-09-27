package com.store.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

	Optional<Permission> findByCode(String code);

	boolean existsByCode(String code);

	List<Permission> findAllByCodeIn(Set<String> codes);

	@Query("""
			    SELECT p FROM Permission p
			    WHERE p.code LIKE CONCAT(:prefix, '%')
			""")
	List<Permission> findAllByCodePrefix(@Param("prefix") String prefix);

	// ✅ FIXED: navigate from Role → permissions (owning side)
	@Query("""
			    SELECT p FROM Role r
			    JOIN r.permissions p
			    WHERE r.id = :roleId
			""")
	List<Permission> findAllByRoleId(@Param("roleId") Long roleId);

	// ✅ FIXED: navigate from UserRole → Role → permissions
	@Query("""
			    SELECT DISTINCT p FROM UserRole ur
			    JOIN ur.role r
			    JOIN r.permissions p
			    WHERE ur.user.id = :userId
			""")
	List<Permission> findAllByUserId(@Param("userId") Long userId);
}