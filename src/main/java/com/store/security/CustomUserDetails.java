package com.store.security;

import com.store.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CustomUserDetails implements UserDetails {

	private final Long id;
	private final String email;
	private final String passwordHash;
	private final boolean enabled;
	private final Set<String> roles; // e.g. {"ADMIN", "VENDOR"}
	private final Set<String> permissions; // e.g. {"product.create", "user.read"}

	public CustomUserDetails(Long id, String email, String passwordHash, boolean enabled, Set<String> roles,
			Set<String> permissions) {
		this.id = id;
		this.email = email;
		this.passwordHash = passwordHash;
		this.enabled = enabled;
		this.roles = roles;
		this.permissions = permissions;
	}

	public static CustomUserDetails from(User user, Set<String> roles, Set<String> permissions) {
		return new CustomUserDetails(user.getId(), user.getEmail(), user.getPasswordHash(),
				user.getStatus() == com.store.user.UserStatusEnum.ACTIVE, roles, permissions);
	}

	public Long getId() {
		return id;
	}

	public Set<String> getRoles() {
		return roles;
	}

	public Set<String> getPermissions() {
		return permissions;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// Roles as ROLE_X and permissions as raw strings
		return Stream.concat(roles.stream().map(r -> new SimpleGrantedAuthority("ROLE_" + r)),
				permissions.stream().map(SimpleGrantedAuthority::new)).collect(Collectors.toSet());
	}

	@Override
	public String getPassword() {
		return passwordHash;
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}
}