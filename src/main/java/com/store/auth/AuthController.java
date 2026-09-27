package com.store.auth;

import com.store.auth.dto.AssignRoleRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/authz")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	// --- Assign / revoke roles ---

	@PostMapping("/roles/assign")
	@PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
	public ResponseEntity<Void> assignRole(@Valid @RequestBody AssignRoleRequest request) {
		authService.assignRole(request);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/roles/revoke")
	@PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
	public ResponseEntity<Void> revokeRole(@RequestParam Long userId, @RequestParam String roleName,
			@RequestParam(required = false) Long organizationId) {
		authService.revokeRole(userId, roleName, organizationId);
		return ResponseEntity.noContent().build();
	}

	// --- Read roles & permissions ---

	@GetMapping("/users/{userId}/roles")
	@PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or @authz.isSelf(#userId)")
	public ResponseEntity<Set<String>> getRoles(@PathVariable Long userId) {
		return ResponseEntity.ok(authService.getRolesForUser(userId));
	}

	@GetMapping("/users/{userId}/permissions")
	@PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or @authz.isSelf(#userId)")
	public ResponseEntity<Set<String>> getPermissions(@PathVariable Long userId) {
		return ResponseEntity.ok(authService.getPermissionsForUser(userId));
	}

	// Fast checks (useful for frontend guards)

	@GetMapping("/users/{userId}/has-role")
	@PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
	public ResponseEntity<Boolean> hasRole(@PathVariable Long userId, @RequestParam String roleName) {
		return ResponseEntity.ok(authService.userHasRole(userId, roleName));
	}

	@GetMapping("/users/{userId}/has-permission")
	@PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
	public ResponseEntity<Boolean> hasPermission(@PathVariable Long userId, @RequestParam String permissionCode) {
		return ResponseEntity.ok(authService.userHasPermission(userId, permissionCode));
	}
}