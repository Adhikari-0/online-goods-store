package com.store.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

	private final PermissionRepository permissionRepository;

	public PermissionController(PermissionRepository permissionRepository) {
		this.permissionRepository = permissionRepository;
	}

	@GetMapping
	@PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
	public ResponseEntity<List<Permission>> listAll() {
		return ResponseEntity.ok(permissionRepository.findAll());
	}

	@GetMapping("/{code}")
	@PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
	public ResponseEntity<Permission> getByCode(@PathVariable String code) {
		return permissionRepository.findByCode(code).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@GetMapping("/by-prefix")
	@PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
	public ResponseEntity<List<Permission>> byPrefix(@RequestParam String prefix) {
		return ResponseEntity.ok(permissionRepository.findAllByCodePrefix(prefix));
	}

	@GetMapping("/by-role/{roleId}")
	@PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
	public ResponseEntity<List<Permission>> byRole(@PathVariable Long roleId) {
		return ResponseEntity.ok(permissionRepository.findAllByRoleId(roleId));
	}

	@GetMapping("/by-user/{userId}")
	@PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or @authz.isSelf(#userId)")
	public ResponseEntity<List<Permission>> byUser(@PathVariable Long userId) {
		return ResponseEntity.ok(permissionRepository.findAllByUserId(userId));
	}
}