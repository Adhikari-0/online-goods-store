package com.store.profile;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VendorProfileRepository extends JpaRepository<VendorProfile, Long> {

    Optional<VendorProfile> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    Optional<VendorProfile> findByBusinessName(String businessName);

    List<VendorProfile> findAllByKycStatus(KycStatusEnum status);

    Page<VendorProfile> findAllByKycStatus(KycStatusEnum status, Pageable pageable);

    long countByKycStatus(KycStatusEnum status);

    // --- Search by business name ---

    @Query("""
        SELECT v FROM VendorProfile v
        WHERE LOWER(v.businessName) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    Page<VendorProfile> searchByBusinessName(@Param("keyword") String keyword,
                                             Pageable pageable);

    // --- Find by tax ID ---

    Optional<VendorProfile> findByTaxId(String taxId);

    // --- Fetch with user ---

    @Query("""
        SELECT v FROM VendorProfile v
        JOIN FETCH v.user
        WHERE v.id = :id
    """)
    Optional<VendorProfile> findByIdWithUser(@Param("id") Long id);
}