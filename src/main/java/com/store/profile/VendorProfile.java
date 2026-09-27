package com.store.profile;

import com.store.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "vendor_profiles")
public class VendorProfile {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    @Column(name = "business_name", nullable = false, length = 255)
    private String businessName;

    @Column(name = "tax_id", length = 50)
    private String taxId;

    @Column(name = "commission_pct", precision = 5, scale = 2)
    private BigDecimal commissionPct;

    @Column(name = "payout_account", length = 100)
    private String payoutAccount;

    @Enumerated(EnumType.STRING)
    @Column(name = "kyc_status", length = 30)
    private KycStatusEnum kycStatus = KycStatusEnum.PENDING;

    // Getters & setters omitted
}
