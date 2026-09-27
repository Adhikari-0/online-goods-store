package com.store.profile;

import com.store.profile.dto.DeliveryAgentProfileRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/delivery-agents")
public class DeliveryAgentController {

	private final DeliveryAgentService deliveryAgentService;

	public DeliveryAgentController(DeliveryAgentService deliveryAgentService) {
		this.deliveryAgentService = deliveryAgentService;
	}

	@PostMapping("/{userId}/profile")
	@PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or @authz.isSelf(#userId)")
	public ResponseEntity<DeliveryAgentProfile> createOrUpdate(@PathVariable Long userId,
			@Valid @RequestBody DeliveryAgentProfileRequest request) {
		return ResponseEntity.ok(deliveryAgentService.createOrUpdate(userId, request));
	}

	@GetMapping("/{userId}/profile")
	@PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('DELIVERY_AGENT')")
	public ResponseEntity<DeliveryAgentProfile> getByUserId(@PathVariable Long userId) {
		return ResponseEntity.ok(deliveryAgentService.getByUserId(userId));
	}

	@PatchMapping("/{userId}/location")
	@PreAuthorize("hasRole('DELIVERY_AGENT') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
	public ResponseEntity<DeliveryAgentProfile> updateLocation(@PathVariable Long userId, @RequestParam Double lat,
			@RequestParam Double lng) {
		return ResponseEntity.ok(deliveryAgentService.updateLocation(userId, lat, lng));
	}

	@PatchMapping("/{userId}/status")
	@PreAuthorize("hasRole('DELIVERY_AGENT') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
	public ResponseEntity<DeliveryAgentProfile> updateStatus(@PathVariable Long userId,
			@RequestParam AgentStatusEnum status) {
		return ResponseEntity.ok(deliveryAgentService.updateStatus(userId, status));
	}

	@PatchMapping("/{userId}/availability")
	@PreAuthorize("hasRole('DELIVERY_AGENT') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
	public ResponseEntity<DeliveryAgentProfile> setAvailability(@PathVariable Long userId,
			@RequestParam boolean available) {
		return ResponseEntity.ok(deliveryAgentService.setAvailability(userId, available));
	}
}