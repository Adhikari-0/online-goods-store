package com.store.catalog;

import com.store.catalog.dto.AttributeRequest;
import com.store.catalog.dto.AttributeResponse;
import com.store.catalog.dto.AttributeValueResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/attributes")
public class AttributeController {

    private final AttributeService attributeService;

    public AttributeController(AttributeService attributeService) {
        this.attributeService = attributeService;
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<AttributeResponse> create(@Valid @RequestBody AttributeRequest request) {
        AttributeResponse created = attributeService.create(request);
        return ResponseEntity.created(URI.create("/api/attributes/" + created.id())).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AttributeResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(attributeService.getById(id));
    }

    @GetMapping("/by-code/{code}")
    public ResponseEntity<AttributeResponse> getByCode(@PathVariable String code) {
        return ResponseEntity.ok(attributeService.getByCode(code));
    }

    @GetMapping
    public ResponseEntity<List<AttributeResponse>> listAll() {
        return ResponseEntity.ok(attributeService.listAll());
    }

    @PostMapping("/{attributeId}/values")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<AttributeValueResponse> addValue(
            @PathVariable Long attributeId,
            @RequestParam String value,
            @RequestParam(required = false) Integer sortOrder) {
        return ResponseEntity.ok(attributeService.addValue(attributeId, value, sortOrder));
    }

    @GetMapping("/{attributeId}/values")
    public ResponseEntity<List<AttributeValueResponse>> listValues(@PathVariable Long attributeId) {
        return ResponseEntity.ok(attributeService.listValues(attributeId));
    }

    @DeleteMapping("/values/{valueId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteValue(@PathVariable Long valueId) {
        attributeService.deleteValue(valueId);
        return ResponseEntity.noContent().build();
    }
}