package com.store.profile;

import com.store.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "delivery_agent_profiles")
public class DeliveryAgentProfile {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    @Column(name = "license_number", length = 50)
    private String licenseNumber;

    @Column(name = "vehicle_type", length = 50)
    private String vehicleType;

    @Column(name = "vehicle_number", length = 50)
    private String vehicleNumber;

    @Column(name = "is_available", nullable = false)
    private boolean available = false;

    @Column(name = "current_latitude")
    private Double currentLatitude;

    @Column(name = "current_longitude")
    private Double currentLongitude;

    @Column(name = "last_location_at")
    private Instant lastLocationAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "agent_status", length = 30)
    private AgentStatusEnum status = AgentStatusEnum.OFFLINE;

    // Getters & setters omitted
}
