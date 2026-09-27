package com.store.user;

import com.store.common.dto.PageRequestBuilder;
import com.store.common.dto.PageResponse;
import com.store.security.CustomUserDetails;
import com.store.security.dto.LoginResponse;
import com.store.user.dto.CreateUserRequest;
import com.store.user.dto.UpdateUserRequest;
import com.store.user.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	// --- Create ---

	@PostMapping
	@PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
	public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {
		UserResponse created = userService.create(request);
		return ResponseEntity.created(URI.create("/api/users/" + created.id())).body(created);
	}

	// --- Read ---

	@GetMapping("/{id}")
	@PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or @authz.isSelf(#id)")
	public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
		return ResponseEntity.ok(userService.getById(id));
	}

	// ✅ ONLY ONE /me endpoint
	@GetMapping("/me")
	public ResponseEntity<LoginResponse.UserSummary> getCurrentUser(
			@AuthenticationPrincipal CustomUserDetails principal) {
		if (principal == null) {
			return ResponseEntity.status(401).build();
		}
		return ResponseEntity.ok(new LoginResponse.UserSummary(principal.getId(), principal.getUsername(), null,
				principal.getRoles(), principal.getPermissions()));
	}

	@GetMapping("/by-email")
	@PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
	public ResponseEntity<UserResponse> getByEmail(@RequestParam String email) {
		return ResponseEntity.ok(userService.getByEmail(email));
	}

	@GetMapping
	@PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
	public ResponseEntity<PageResponse<UserResponse>> list(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size, @RequestParam(defaultValue = "id") String sortBy,
			@RequestParam(defaultValue = "asc") String direction) {

		Pageable pageable = PageRequestBuilder.build(page, size, sortBy, direction);
		return ResponseEntity.ok(userService.list(pageable));
	}

	// --- Update ---

	@PatchMapping("/{id}")
	@PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or @authz.isSelf(#id)")
	public ResponseEntity<UserResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
		return ResponseEntity.ok(userService.update(id, request));
	}

	@PatchMapping("/{id}/status")
	@PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
	public ResponseEntity<Void> changeStatus(@PathVariable Long id, @RequestParam UserStatusEnum status) {
		userService.changeStatus(id, status);
		return ResponseEntity.noContent().build();
	}

	// --- Delete ---

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('SUPER_ADMIN')")
	public ResponseEntity<Void> softDelete(@PathVariable Long id) {
		userService.softDelete(id);
		return ResponseEntity.noContent().build();
	}
}