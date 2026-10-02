package com.store.catalog;

import com.store.catalog.dto.BrandRequest;
import com.store.catalog.dto.BrandResponse;
import com.store.common.exception.DuplicateResourceException;
import com.store.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;

    public BrandServiceImpl(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    @Override
    public BrandResponse create(BrandRequest request) {
        String slug = slugify(request.slug() != null ? request.slug() : request.name());
        if (brandRepository.existsBySlug(slug)) {
            throw new DuplicateResourceException("Brand slug already exists: " + slug);
        }

        Brand brand = new Brand();
        brand.setSlug(slug);
        brand.setName(request.name());
        brand.setDescription(request.description());
        brand.setLogoUrl(request.logoUrl());
        brand.setActive(request.active() != null ? request.active() : true);

        return BrandResponse.from(brandRepository.save(brand));
    }

    @Override
    public BrandResponse update(Long id, BrandRequest request) {
        Brand brand = getEntity(id);

        if (request.name() != null) brand.setName(request.name());
        if (request.description() != null) brand.setDescription(request.description());
        if (request.logoUrl() != null) brand.setLogoUrl(request.logoUrl());
        if (request.active() != null) brand.setActive(request.active());

        if (request.slug() != null && !request.slug().equals(brand.getSlug())) {
            String slug = slugify(request.slug());
            if (brandRepository.existsBySlug(slug)) {
                throw new DuplicateResourceException("Brand slug already exists: " + slug);
            }
            brand.setSlug(slug);
        }

        return BrandResponse.from(brandRepository.save(brand));
    }

    @Override
    @Transactional(readOnly = true)
    public BrandResponse getById(Long id) {
        return BrandResponse.from(getEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public BrandResponse getBySlug(String slug) {
        return BrandResponse.from(
            brandRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found: " + slug))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<BrandResponse> listAll() {
        return brandRepository.findAllByActiveTrue().stream()
            .map(BrandResponse::from)
            .toList();
    }

    @Override
    public void delete(Long id) {
        brandRepository.delete(getEntity(id));
    }

    private Brand getEntity(Long id) {
        return brandRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Brand", id));
    }

    private static String slugify(String input) {
        if (input == null) return "";
        return input.toLowerCase().trim()
            .replaceAll("[^a-z0-9]+", "-")
            .replaceAll("-+", "-")
            .replaceAll("^-+|-+$", "");
    }
}