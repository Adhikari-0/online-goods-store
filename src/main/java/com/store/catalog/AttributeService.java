package com.store.catalog;

import com.store.catalog.dto.AttributeRequest;
import com.store.catalog.dto.AttributeResponse;
import com.store.catalog.dto.AttributeValueResponse;

import java.util.List;

public interface AttributeService {

    AttributeResponse create(AttributeRequest request);

    AttributeResponse getById(Long id);

    AttributeResponse getByCode(String code);

    List<AttributeResponse> listAll();

    AttributeValueResponse addValue(Long attributeId, String value, Integer sortOrder);

    List<AttributeValueResponse> listValues(Long attributeId);

    void deleteValue(Long valueId);
}