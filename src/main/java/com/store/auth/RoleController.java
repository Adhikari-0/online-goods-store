package com.store.auth;

import com.store.auth.dto.RoleResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

	private final RoleService roleService;

	public RoleController(RoleService roleService) {
		this.roleService = roleService;
	}

	// --- Create ---

	@PostMapping
	@PreAuthorize("hasRole('SUPER_ADMIN')")
	public ResponseEntity<RoleResponse> create(@Valid @RequestBody CreateRoleRequest request) {
		RoleResponse created = roleService.create(request.name(), request.description(), request.permissionCodes());
		return ResponseEntity.created(URI.create("/api/roles/" + created.id())).body(created);
	}

	// --- Read ---

	@GetMapping("/{id}")
	@PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
	public ResponseEntity<RoleResponse> getById(@PathVariable Long id) {
		return ResponseEntity.ok(roleService.getById(id));
	}

	@GetMapping("/by-name/{name}")
	@PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
	public ResponseEntity<RoleResponse> getByName(@PathVariable String name) {
		return ResponseEntity.ok(roleService.getByName(name));
	}

	@GetMapping
	@PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
	public ResponseEntity<List<RoleResponse>> listAll() {
		return ResponseEntity.ok(roleService.listAll());
	}

	// --- Update permissions ---

	@PutMapping("/{id}/permissions")
	@PreAuthorize("hasRole('SUPER_ADMIN')")
	public ResponseEntity<RoleResponse> updatePermissions(@PathVariable Long id,
			@RequestBody Set<String> permissionCodes) {
		return ResponseEntity.ok(roleService.updatePermissions(id, permissionCodes));
	}

	// --- Delete ---

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('SUPER_ADMIN')")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		roleService.delete(id);
		return ResponseEntity.noContent().build();
	}

	// --- Request DTO ---

	public record CreateRoleRequest(@NotBlank String name, String description, Set<String> permissionCodes) {
	}
}