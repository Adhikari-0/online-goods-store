package com.store.user;

import com.store.auth.Role;
import com.store.auth.RoleRepository;
import com.store.auth.UserRole;
import com.store.auth.UserRoleRepository;
import com.store.common.dto.PageResponse;
import com.store.common.exception.DuplicateResourceException;
import com.store.common.exception.ResourceNotFoundException;
import com.store.user.dto.CreateUserRequest;
import com.store.user.dto.UpdateUserRequest;
import com.store.user.dto.UserResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final UserProfileRepository profileRepository;
	private final RoleRepository roleRepository;
	private final UserRoleRepository userRoleRepository;
	private final PasswordEncoder passwordEncoder;

	public UserServiceImpl(UserRepository userRepository, UserProfileRepository profileRepository,
			RoleRepository roleRepository, UserRoleRepository userRoleRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.profileRepository = profileRepository;
		this.roleRepository = roleRepository;
		this.userRoleRepository = userRoleRepository;
		this.passwordEncoder = passwordEncoder;
	}

	// Creating User by Super-Admin
	@Override
	public UserResponse create(CreateUserRequest request) {
		if (userRepository.existsByEmail(request.email())) {
			throw new DuplicateResourceException("Email already registered: " + request.email());
		}

		User user = new User();
		user.setEmail(request.email().toLowerCase().trim());
		user.setPasswordHash(passwordEncoder.encode(request.password()));
		user.setFullName(request.fullName());
		user.setPhone(request.phone());
		user.setStatus(UserStatusEnum.PENDING_VERIFICATION);

		User saved = userRepository.save(user);

		// Create empty profile
		UserProfile profile = new UserProfile();
		profile.setUser(saved);
		profileRepository.save(profile);

		// Auto-assign USER role
		assignDefaultUserRole(saved);

		return toResponse(saved);
	}

	/**
	 * Assigns the base USER role to a newly created user. If the USER role doesn't
	 * exist yet, silently skips.
	 */
	private void assignDefaultUserRole(User user) {
		roleRepository.findByName("USER").ifPresent(role -> {
			UserRole userRole = new UserRole();
			userRole.setUser(user);
			userRole.setRole(role);
			userRole.setGrantedBy("system");
			userRole.setGrantedAt(Instant.now());
			userRoleRepository.save(userRole);

			// Keep in-memory object in sync so toResponse() sees the role
			user.getUserRoles().add(userRole);
		});
	}

	@Override
	@Transactional(readOnly = true)
	public UserResponse getById(Long id) {
		return toResponse(getEntityById(id));
	}

	@Override
	@Transactional(readOnly = true)
	public UserResponse getByEmail(String email) {
		User user = userRepository.findByEmail(email.toLowerCase().trim())
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
		return toResponse(user);
	}

	@Override
	@Transactional(readOnly = true)
	public PageResponse<UserResponse> list(Pageable pageable) {
		return PageResponse.of(userRepository.findAll(pageable), this::toResponse);
	}

	@Override
	public UserResponse update(Long id, UpdateUserRequest request) {
		User user = getEntityById(id);

		if (request.fullName() != null)
			user.setFullName(request.fullName());
		if (request.phone() != null)
			user.setPhone(request.phone());
		if (request.avatarUrl() != null)
			user.setAvatarUrl(request.avatarUrl());

		return toResponse(userRepository.save(user));
	}

	@Override
	public void changeStatus(Long id, UserStatusEnum status) {
		User user = getEntityById(id);
		user.setStatus(status);
		userRepository.save(user);
	}

	@Override
	public void softDelete(Long id) {
		User user = getEntityById(id);
		user.setStatus(UserStatusEnum.DELETED);
		userRepository.save(user);
	}

	@Override
	public void recordLogin(Long id) {
		User user = getEntityById(id);
		user.setLastLoginAt(Instant.now());
		userRepository.save(user);
	}

	@Override
	@Transactional(readOnly = true)
	public User getEntityById(Long id) {
		return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", id));
	}

	// --- Mapper ---

	private UserResponse toResponse(User user) {
		Set<String> roles = user.getUserRoles().stream().map(ur -> ur.getRole().getName()).collect(Collectors.toSet());

		return new UserResponse(user.getId(), user.getEmail(), user.getFullName(), user.getPhone(), user.getAvatarUrl(),
				user.getStatus(), user.isEmailVerified(), roles, user.getCreatedAt(), user.getLastLoginAt());
	}
}