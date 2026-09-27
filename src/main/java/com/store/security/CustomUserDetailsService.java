package com.store.security;

import com.store.user.User;
import com.store.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	public CustomUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findByEmailWithRoles(email.toLowerCase().trim())
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

		Set<String> roles = user.getUserRoles().stream().map(ur -> ur.getRole().getName()).collect(Collectors.toSet());

		Set<String> permissions = user.getUserRoles().stream().flatMap(ur -> ur.getRole().getPermissions().stream())
				.map(p -> p.getCode()).collect(Collectors.toSet());

		return CustomUserDetails.from(user, roles, permissions);
	}

	@Transactional(readOnly = true)
	public UserDetails loadUserById(Long id) {
		User user = userRepository.findByIdWithRoles(id)
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + id));

		Set<String> roles = user.getUserRoles().stream().map(ur -> ur.getRole().getName()).collect(Collectors.toSet());

		Set<String> permissions = user.getUserRoles().stream().flatMap(ur -> ur.getRole().getPermissions().stream())
				.map(p -> p.getCode()).collect(Collectors.toSet());

		return CustomUserDetails.from(user, roles, permissions);
	}
}