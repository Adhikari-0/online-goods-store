package com.store.auth;

import com.store.organization.Organization;
import com.store.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "user_roles", uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "role_id",
		"organization_id" }), indexes = { @Index(name = "idx_user_roles_user", columnList = "user_id"),
				@Index(name = "idx_user_roles_org", columnList = "organization_id") })
public class UserRole {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "role_id", nullable = false)
	private Role role;

	// Optional: null = global role, non-null = scoped to an org
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "organization_id")
	private Organization organization;

	@Column(name = "granted_at", nullable = false, updatable = false)
	private Instant grantedAt;

	@Column(name = "granted_by", length = 255)
	private String grantedBy;

	@Column(name = "expires_at")
	private Instant expiresAt;

	@PrePersist
	void onCreate() {
		this.grantedAt = Instant.now();
	}

	// Getters & setters omitted
}