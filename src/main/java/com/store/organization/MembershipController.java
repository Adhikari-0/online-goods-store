package com.store.organization;

import com.store.organization.dto.MembershipResponse;
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
    public ResponseEntity<MembershipResponse> addMember(
            @PathVariable Long orgId,
            @PathVariable Long userId) {
        Membership m = membershipService.addMember(userId, orgId);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(MembershipResponse.from(m));
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<MembershipResponse> getMembership(
            @PathVariable Long orgId,
            @PathVariable Long userId) {
        return ResponseEntity.ok(
            MembershipResponse.from(membershipService.getMembership(userId, orgId))
        );
    }

    @PatchMapping("/{userId}/suspend")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<MembershipResponse> suspend(
            @PathVariable Long orgId,
            @PathVariable Long userId) {
        return ResponseEntity.ok(
            MembershipResponse.from(membershipService.suspendMember(userId, orgId))
        );
    }

    @PatchMapping("/{userId}/reactivate")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<MembershipResponse> reactivate(
            @PathVariable Long orgId,
            @PathVariable Long userId) {
        return ResponseEntity.ok(
            MembershipResponse.from(membershipService.reactivateMember(userId, orgId))
        );
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<Void> remove(
            @PathVariable Long orgId,
            @PathVariable Long userId) {
        membershipService.removeMember(userId, orgId);
        return ResponseEntity.noContent().build();
    }
}