package com.store.organization;

import com.store.common.exception.DuplicateResourceException;
import com.store.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;

    public OrganizationServiceImpl(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    @Override
    public Organization create(String slug, String name) {
        if (organizationRepository.existsBySlug(slug)) {
            throw new DuplicateResourceException("Organization slug already exists: " + slug);
        }
        Organization org = new Organization();
        org.setSlug(slug.toLowerCase().trim());
        org.setName(name);
        org.setStatus(OrganizationStatusEnum.ACTIVE);
        return organizationRepository.save(org);
    }

    @Override
    @Transactional(readOnly = true)
    public Organization getById(Long id) {
        return getEntityById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Organization getBySlug(String slug) {
        return organizationRepository.findBySlug(slug)
            .orElseThrow(() -> new ResourceNotFoundException("Organization not found: " + slug));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Organization> list(Pageable pageable) {
        return organizationRepository.findAll(pageable);
    }

    @Override
    public Organization updateStatus(Long id, OrganizationStatusEnum status) {
        Organization org = getEntityById(id);
        org.setStatus(status);
        return organizationRepository.save(org);
    }

    @Override
    @Transactional(readOnly = true)
    public Organization getEntityById(Long id) {
        return organizationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Organization", id));
    }
}