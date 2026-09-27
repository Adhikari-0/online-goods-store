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
public interface DeliveryAgentProfileRepository
        extends JpaRepository<DeliveryAgentProfile, Long> {

    Optional<DeliveryAgentProfile> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    Optional<DeliveryAgentProfile> findByLicenseNumber(String licenseNumber);

    Optional<DeliveryAgentProfile> findByVehicleNumber(String vehicleNumber);

    // --- Availability queries ---

    List<DeliveryAgentProfile> findAllByAvailable(boolean available);

    List<DeliveryAgentProfile> findAllByStatus(AgentStatusEnum status);

    List<DeliveryAgentProfile> findAllByAvailableAndStatus(
        boolean available, AgentStatusEnum status);

    Page<DeliveryAgentProfile> findAllByStatus(AgentStatusEnum status, Pageable pageable);

    long countByStatus(AgentStatusEnum status);

    long countByAvailableAndStatus(boolean available, AgentStatusEnum status);

    // --- Geo query: find nearby available agents ---
    // Note: real geo search usually uses PostGIS/MySQL spatial.
    // This is a simple bounding-box approximation.

    @Query("""
        SELECT d FROM DeliveryAgentProfile d
        WHERE d.available = true
          AND d.status = 'AVAILABLE'
          AND d.currentLatitude BETWEEN :minLat AND :maxLat
          AND d.currentLongitude BETWEEN :minLng AND :maxLng
    """)
    List<DeliveryAgentProfile> findAvailableAgentsInBoundingBox(
        @Param("minLat") double minLat,
        @Param("maxLat") double maxLat,
        @Param("minLng") double minLng,
        @Param("maxLng") double maxLng);

    // --- Fetch with user ---

    @Query("""
        SELECT d FROM DeliveryAgentProfile d
        JOIN FETCH d.user
        WHERE d.id = :id
    """)
    Optional<DeliveryAgentProfile> findByIdWithUser(@Param("id") Long id);

    // --- Inactive agents (no location update recently) ---

    @Query("""
        SELECT d FROM DeliveryAgentProfile d
        WHERE d.status = 'AVAILABLE'
          AND (d.lastLocationAt IS NULL OR d.lastLocationAt < :cutoff)
    """)
    List<DeliveryAgentProfile> findStaleAgents(@Param("cutoff") java.time.Instant cutoff);
}