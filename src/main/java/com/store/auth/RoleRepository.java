package com.store.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

	Optional<Role> findByName(String name);

	boolean existsByName(String name);

	List<Role> findAllBySystem(boolean system);

	// --- Fetch with permissions (avoids N+1) ---

	@Query("""
			    SELECT DISTINCT r FROM Role r
			    LEFT JOIN FETCH r.permissions
			    WHERE r.name = :name
			""")
	Optional<Role> findByNameWithPermissions(@Param("name") String name);

	@Query("""
			    SELECT DISTINCT r FROM Role r
			    LEFT JOIN FETCH r.permissions
			    WHERE r.id = :id
			""")
	Optional<Role> findByIdWithPermissions(@Param("id") Long id);

	@Query("""
			    SELECT DISTINCT r FROM Role r
			    LEFT JOIN FETCH r.permissions
			""")
	List<Role> findAllWithPermissions();

	// --- Bulk name lookup ---

	List<Role> findAllByNameIn(Set<String> names);
}