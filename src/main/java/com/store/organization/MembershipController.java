package com.store.organization;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/organizations/{orgId}/members")
public class MembershipController {

	private final MembershipService membershipService;

	public MembershipController(MembershipService membershipService) {
		this.membershipService = membershipService;
	}

	@PostMapping("/{userId}")
	@PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
	public ResponseEntity<Membership> addMember(@PathVariable Long orgId, @PathVariable Long userId) {
		return ResponseEntity.status(HttpStatus.CREATED).body(membershipService.addMember(userId, orgId));
	}

	@GetMapping("/{userId}")
	@PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
	public ResponseEntity<Membership> getMembership(@PathVariable Long orgId, @PathVariable Long userId) {
		return ResponseEntity.ok(membershipService.getMembership(userId, orgId));
	}

	@PatchMapping("/{userId}/suspend")
	@PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
	public ResponseEntity<Membership> suspend(@PathVariable Long orgId, @PathVariable Long userId) {
		return ResponseEntity.ok(membershipService.suspendMember(userId, orgId));
	}

	@PatchMapping("/{userId}/reactivate")
	@PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
	public ResponseEntity<Membership> reactivate(@PathVariable Long orgId, @PathVariable Long userId) {
		return ResponseEntity.ok(membershipService.reactivateMember(userId, orgId));
	}

	@DeleteMapping("/{userId}")
	@PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
	public ResponseEntity<Void> remove(@PathVariable Long orgId, @PathVariable Long userId) {
		membershipService.removeMember(userId, orgId);
		return ResponseEntity.noContent().build();
	}
}