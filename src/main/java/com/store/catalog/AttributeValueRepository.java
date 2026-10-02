package com.store.catalog;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttributeValueRepository extends JpaRepository<AttributeValue, Long> {

    List<AttributeValue> findAllByAttributeIdOrderBySortOrderAsc(Long attributeId);
}