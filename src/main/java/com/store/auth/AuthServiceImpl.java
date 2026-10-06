package com.store.auth;

import com.store.auth.dto.AssignRoleRequest;
import com.store.common.exception.BusinessException;
import com.store.common.exception.ResourceNotFoundException;
import com.store.organization.MembershipRepository;
import com.store.organization.Organization;
import com.store.organization.OrganizationRepository;
import com.store.user.User;
import com.store.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final UserRoleRepository userRoleRepository;
	private final OrganizationRepository organizationRepository;
	private final MembershipRepository membershipRepository;

	public AuthServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
			UserRoleRepository userRoleRepository, OrganizationRepository organizationRepository,
			MembershipRepository membershipRepository) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.userRoleRepository = userRoleRepository;
		this.organizationRepository = organizationRepository;
		this.membershipRepository = membershipRepository;
	}

	@Override
	public void assignRole(AssignRoleRequest request) {
		User user = userRepository.findById(request.userId())
				.orElseThrow(() -> new ResourceNotFoundException("User", request.userId()));

		Role role = roleRepository.findByName(request.roleName())
				.orElseThrow(() -> new ResourceNotFoundException("Role not found: " + request.roleName()));

		Organization org = null;
		if (request.organizationId() != null) {
			org = organizationRepository.findById(request.organizationId())
					.orElseThrow(() -> new ResourceNotFoundException("Organization", request.organizationId()));

			// User must be an active member of the organization
			if (!membershipRepository.existsByUserIdAndOrganizationIdAndState(user.getId(), org.getId(),
					com.store.organization.MembershipStateEnum.ACTIVE)) {
				throw new BusinessException("User is not an active member of the organization");
			}
		}

		// Idempotency check
		if (userRoleRepository.existsByUserIdAndRoleIdAndOrganizationId(user.getId(), role.getId(),
				org != null ? org.getId() : null)) {
			return; // already assigned
		}

		UserRole userRole = new UserRole();
		userRole.setUser(user);
		userRole.setRole(role);
		userRole.setOrganization(org);
		userRole.setGrantedBy(request.grantedBy());
		userRole.setGrantedAt(Instant.now());

		userRoleRepository.save(userRole);
	}

	@Override
	public void revokeRole(Long userId, String roleName, Long organizationId) {
		Role role = roleRepository.findByName(roleName.toUpperCase().trim())
				.orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));

		int deleted = userRoleRepository.deleteByUserIdAndRoleIdAndOrganizationId(userId, role.getId(), organizationId);

		if (deleted == 0) {
			throw new ResourceNotFoundException("User " + userId + " does not have role " + roleName);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Set<String> getRolesForUser(Long userId) {
		return userRoleRepository.findAllByUserId(userId).stream().map(ur -> ur.getRole().getName())
				.collect(Collectors.toSet());
	}

	@Override
	@Transactional(readOnly = true)
	public Set<String> getPermissionsForUser(Long userId) {
		return userRoleRepository.findAllByUserId(userId).stream().flatMap(ur -> ur.getRole().getPermissions().stream())
				.map(Permission::getCode).collect(Collectors.toSet());
	}

	@Override
	@Transactional(readOnly = true)
	public boolean userHasRole(Long userId, String roleName) {
		return userRoleRepository.findAllByUserId(userId).stream()
				.anyMatch(ur -> ur.getRole().getName().equals(roleName));
	}

	@Override
	@Transactional(readOnly = true)
	public boolean userHasPermission(Long userId, String permissionCode) {
		return userRoleRepository.findAllByUserId(userId).stream().flatMap(ur -> ur.getRole().getPermissions().stream())
				.anyMatch(p -> p.getCode().equals(permissionCode));
	}
}