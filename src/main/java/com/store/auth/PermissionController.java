package com.store.auth;

import com.store.auth.dto.PermissionResponse;
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
    public ResponseEntity<List<PermissionResponse>> listAll() {
        return ResponseEntity.ok(
            permissionRepository.findAll().stream()
                .map(PermissionResponse::from)
                .toList()
        );
    }

    @GetMapping("/{code}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<PermissionResponse> getByCode(@PathVariable String code) {
        return permissionRepository.findByCode(code)
            .map(PermissionResponse::from)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-prefix")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<List<PermissionResponse>> byPrefix(@RequestParam String prefix) {
        return ResponseEntity.ok(
            permissionRepository.findAllByCodePrefix(prefix).stream()
                .map(PermissionResponse::from)
                .toList()
        );
    }

    @GetMapping("/by-role/{roleId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<List<PermissionResponse>> byRole(@PathVariable Long roleId) {
        return ResponseEntity.ok(
            permissionRepository.findAllByRoleId(roleId).stream()
                .map(PermissionResponse::from)
                .toList()
        );
    }

    @GetMapping("/by-user/{userId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or @authz.isSelf(#userId)")
    public ResponseEntity<List<PermissionResponse>> byUser(@PathVariable Long userId) {
        return ResponseEntity.ok(
            permissionRepository.findAllByUserId(userId).stream()
                .map(PermissionResponse::from)
                .toList()
        );
    }
}