package com.store.catalog;

import com.store.catalog.dto.CategoryRequest;
import com.store.catalog.dto.CategoryResponse;
import com.store.common.exception.BusinessException;
import com.store.common.exception.DuplicateResourceException;
import com.store.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public CategoryResponse create(CategoryRequest request) {
        String slug = slugify(request.slug() != null ? request.slug() : request.name());
        if (categoryRepository.existsBySlug(slug)) {
            throw new DuplicateResourceException("Category slug already exists: " + slug);
        }

        Category category = new Category();
        category.setSlug(slug);
        category.setName(request.name());
        category.setDescription(request.description());
        category.setSortOrder(request.sortOrder() != null ? request.sortOrder() : 0);
        category.setActive(request.active() != null ? request.active() : true);
        category.setImageUrl(request.imageUrl());

        if (request.parentId() != null) {
            Category parent = categoryRepository.findById(request.parentId())
                .orElseThrow(() -> new ResourceNotFoundException("Parent category", request.parentId()));
            category.setParent(parent);
        }

        return CategoryResponse.from(categoryRepository.save(category), false);
    }

    @Override
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = getEntity(id);

        if (request.name() != null) category.setName(request.name());
        if (request.description() != null) category.setDescription(request.description());
        if (request.sortOrder() != null) category.setSortOrder(request.sortOrder());
        if (request.active() != null) category.setActive(request.active());
        if (request.imageUrl() != null) category.setImageUrl(request.imageUrl());

        if (request.slug() != null && !request.slug().equals(category.getSlug())) {
            String slug = slugify(request.slug());
            if (categoryRepository.existsBySlug(slug)) {
                throw new DuplicateResourceException("Category slug already exists: " + slug);
            }
            category.setSlug(slug);
        }

        if (request.parentId() != null) {
            if (request.parentId().equals(id)) {
                throw new BusinessException("Category cannot be its own parent");
            }
            Category parent = categoryRepository.findById(request.parentId())
                .orElseThrow(() -> new ResourceNotFoundException("Parent category", request.parentId()));
            category.setParent(parent);
        }

        return CategoryResponse.from(categoryRepository.save(category), false);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getById(Long id) {
        return CategoryResponse.from(getEntity(id), true);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getBySlug(String slug) {
        Category c = categoryRepository.findBySlug(slug)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + slug));
        return CategoryResponse.from(c, true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> listAll() {
        return categoryRepository.findAllByActiveTrue().stream()
            .map(c -> CategoryResponse.from(c, false))
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> listTree() {
        return categoryRepository.findRootCategoriesWithChildren().stream()
            .map(c -> CategoryResponse.from(c, true))
            .toList();
    }

    @Override
    public void delete(Long id) {
        Category category = getEntity(id);
        if (!category.getChildren().isEmpty()) {
            throw new BusinessException("Cannot delete category with subcategories");
        }
        categoryRepository.delete(category);
    }

    private Category getEntity(Long id) {
        return categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category", id));
    }

    private static String slugify(String input) {
        if (input == null) return "";
        return input.toLowerCase().trim()
            .replaceAll("[^a-z0-9]+", "-")
            .replaceAll("-+", "-")
            .replaceAll("^-+|-+$", "");
    }
}