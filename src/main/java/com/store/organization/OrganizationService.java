package com.store.organization;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrganizationService {

    Organization create(String slug, String name);

    Organization getById(Long id);

    Organization getBySlug(String slug);

    Page<Organization> list(Pageable pageable);

    Organization updateStatus(Long id, OrganizationStatusEnum status);

    Organization getEntityById(Long id);
}