package com.store.catalog;

import com.store.catalog.dto.ProductRequest;
import com.store.catalog.dto.ProductResponse;
import com.store.catalog.dto.ProductSummaryResponse;
import com.store.common.dto.PageResponse;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    ProductResponse create(Long vendorId, ProductRequest request);

    ProductResponse update(Long id, ProductRequest request);

    ProductResponse getById(Long id);

    ProductResponse getBySlug(String slug);

    PageResponse<ProductSummaryResponse> list(Pageable pageable);

    PageResponse<ProductSummaryResponse> listByStatus(ProductStatusEnum status, Pageable pageable);

    PageResponse<ProductSummaryResponse> listByVendor(Long vendorId, Pageable pageable);

    PageResponse<ProductSummaryResponse> listByCategory(Long categoryId, Pageable pageable);

    PageResponse<ProductSummaryResponse> listByBrand(Long brandId, Pageable pageable);

    PageResponse<ProductSummaryResponse> search(String keyword, Pageable pageable);

    ProductResponse changeStatus(Long id, ProductStatusEnum status);

    void delete(Long id);
}