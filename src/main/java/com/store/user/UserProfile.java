package com.store.user;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user_profiles")
public class UserProfile {

	@Id
	private Long id; // shares PK with users.id

	@OneToOne(fetch = FetchType.LAZY)
	@MapsId
	@JoinColumn(name = "id")
	private User user;

	@Column(name = "date_of_birth")
	private LocalDate dateOfBirth;

	@Column(length = 20)
	private String gender;

	@Column(length = 500)
	private String address;

	@Column(length = 100)
	private String city;

	@Column(length = 100)
	private String country;

	@Column(name = "postal_code", length = 20)
	private String postalCode;

	@Lob
	@Column(name = "bio")
	private String bio;

}
