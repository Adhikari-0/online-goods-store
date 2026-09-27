package com.store.organization;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    Optional<Organization> findBySlug(String slug);

    boolean existsBySlug(String slug);

    Page<Organization> findAllByStatus(OrganizationStatusEnum status, Pageable pageable);

    List<Organization> findAllByStatus(OrganizationStatusEnum status);

    // --- Search by name ---

    @Query("""
        SELECT o FROM Organization o
        WHERE LOWER(o.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(o.slug) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    Page<Organization> search(@Param("keyword") String keyword, Pageable pageable);

    // --- Organizations for a user ---

    @Query("""
        SELECT DISTINCT o FROM Organization o
        JOIN Membership m ON m.organization = o
        WHERE m.user.id = :userId
          AND m.state = 'ACTIVE'
    """)
    List<Organization> findAllByUserId(@Param("userId") Long userId);

    long countByStatus(OrganizationStatusEnum status);
}