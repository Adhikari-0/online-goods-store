package com.store.catalog;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VariantAttributeValueRepository extends JpaRepository<VariantAttributeValue, Long> {

    List<VariantAttributeValue> findAllByVariantId(Long variantId);

    void deleteAllByVariantId(Long variantId);
}