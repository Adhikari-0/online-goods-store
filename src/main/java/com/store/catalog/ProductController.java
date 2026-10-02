package com.store.catalog;

import com.store.catalog.dto.ProductRequest;
import com.store.catalog.dto.ProductResponse;
import com.store.catalog.dto.ProductSummaryResponse;
import com.store.common.dto.PageRequestBuilder;
import com.store.common.dto.PageResponse;
import com.store.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @PreAuthorize("hasRole('VENDOR') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ProductResponse> create(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody ProductRequest request) {
        ProductResponse created = productService.create(principal.getId(), request);
        return ResponseEntity.created(URI.create("/api/products/" + created.id())).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    @GetMapping("/by-slug/{slug}")
    public ResponseEntity<ProductResponse> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(productService.getBySlug(slug));
    }

    @GetMapping
    public ResponseEntity<PageResponse<ProductSummaryResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Pageable pageable = PageRequestBuilder.build(page, size, sortBy, direction);
        return ResponseEntity.ok(productService.list(pageable));
    }

    @GetMapping("/by-status")
    public ResponseEntity<PageResponse<ProductSummaryResponse>> listByStatus(
            @RequestParam ProductStatusEnum status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(productService.listByStatus(status, PageRequestBuilder.build(page, size)));
    }

    @GetMapping("/by-vendor/{vendorId}")
    public ResponseEntity<PageResponse<ProductSummaryResponse>> listByVendor(
            @PathVariable Long vendorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(productService.listByVendor(vendorId, PageRequestBuilder.build(page, size)));
    }

    @GetMapping("/by-category/{categoryId}")
    public ResponseEntity<PageResponse<ProductSummaryResponse>> listByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(productService.listByCategory(categoryId, PageRequestBuilder.build(page, size)));
    }

    @GetMapping("/by-brand/{brandId}")
    public ResponseEntity<PageResponse<ProductSummaryResponse>> listByBrand(
            @PathVariable Long brandId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(productService.listByBrand(brandId, PageRequestBuilder.build(page, size)));
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponse<ProductSummaryResponse>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(productService.search(keyword, PageRequestBuilder.build(page, size)));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('VENDOR') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ProductResponse> update(@PathVariable Long id,
                                                  @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.update(id, request));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ProductResponse> changeStatus(@PathVariable Long id,
                                                        @RequestParam ProductStatusEnum status) {
        return ResponseEntity.ok(productService.changeStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}