package com.store.profile;

import com.store.profile.dto.VendorProfileRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vendors")
public class VendorController {

	private final VendorService vendorService;

	public VendorController(VendorService vendorService) {
		this.vendorService = vendorService;
	}

	@PostMapping("/{userId}/profile")
	@PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or @authz.isSelf(#userId)")
	public ResponseEntity<VendorProfile> createOrUpdate(@PathVariable Long userId,
			@Valid @RequestBody VendorProfileRequest request) {
		return ResponseEntity.ok(vendorService.createOrUpdate(userId, request));
	}

	@GetMapping("/{userId}/profile")
	@PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('VENDOR')")
	public ResponseEntity<VendorProfile> getByUserId(@PathVariable Long userId) {
		return ResponseEntity.ok(vendorService.getByUserId(userId));
	}

	@PatchMapping("/{userId}/kyc")
	@PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
	public ResponseEntity<VendorProfile> updateKyc(@PathVariable Long userId, @RequestParam KycStatusEnum status) {
		return ResponseEntity.ok(vendorService.updateKycStatus(userId, status));
	}
}