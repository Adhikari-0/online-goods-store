package com.store.catalog;

import com.store.catalog.dto.CategoryRequest;
import com.store.catalog.dto.CategoryResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse create(CategoryRequest request);

    CategoryResponse update(Long id, CategoryRequest request);

    CategoryResponse getById(Long id);

    CategoryResponse getBySlug(String slug);

    List<CategoryResponse> listAll();

    List<CategoryResponse> listTree();

    void delete(Long id);
}