package com.store.catalog;

import com.store.catalog.dto.BrandRequest;
import com.store.catalog.dto.BrandResponse;

import java.util.List;

public interface BrandService {

    BrandResponse create(BrandRequest request);

    BrandResponse update(Long id, BrandRequest request);

    BrandResponse getById(Long id);

    BrandResponse getBySlug(String slug);

    List<BrandResponse> listAll();

    void delete(Long id);
}