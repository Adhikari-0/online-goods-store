package com.store.organization;

import com.store.common.dto.PageRequestBuilder;
import com.store.common.dto.PageResponse;
import com.store.organization.dto.OrganizationResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {

	private final OrganizationService organizationService;

	public OrganizationController(OrganizationService organizationService) {
		this.organizationService = organizationService;
	}

	// --- Create ---

	@PostMapping
	@PreAuthorize("hasRole('SUPER_ADMIN')")
	public ResponseEntity<OrganizationResponse> create(@Valid @RequestBody CreateOrgRequest request) {
		Organization created = organizationService.create(request.slug(), request.name());
		return ResponseEntity.created(URI.create("/api/organizations/" + created.getId()))
				.body(OrganizationResponse.from(created));
	}

	// --- Read ---

	@GetMapping("/{id}")
	@PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
	public ResponseEntity<OrganizationResponse> getById(@PathVariable Long id) {
		return ResponseEntity.ok(OrganizationResponse.from(organizationService.getById(id)));
	}

	@GetMapping("/by-slug/{slug}")
	@PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
	public ResponseEntity<OrganizationResponse> getBySlug(@PathVariable String slug) {
		return ResponseEntity.ok(OrganizationResponse.from(organizationService.getBySlug(slug)));
	}

	@GetMapping
	@PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
	public ResponseEntity<PageResponse<OrganizationResponse>> list(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size, @RequestParam(defaultValue = "id") String sortBy,
			@RequestParam(defaultValue = "asc") String direction) {

		Pageable pageable = PageRequestBuilder.build(page, size, sortBy, direction);
		return ResponseEntity.ok(PageResponse.of(organizationService.list(pageable), OrganizationResponse::from));
	}

	// --- Update ---

	@PatchMapping("/{id}/status")
	@PreAuthorize("hasRole('SUPER_ADMIN')")
	public ResponseEntity<OrganizationResponse> updateStatus(@PathVariable Long id,
			@RequestParam OrganizationStatusEnum status) {
		return ResponseEntity.ok(OrganizationResponse.from(organizationService.updateStatus(id, status)));
	}

	// --- Request DTO

	public record CreateOrgRequest(@NotBlank String slug, @NotBlank String name) {
	}
}