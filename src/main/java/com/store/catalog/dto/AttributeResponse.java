package com.store.catalog.dto;

import com.store.catalog.Attribute;

public record AttributeResponse(
    Long id,
    String code,
    String name,
    Integer sortOrder
) {
    public static AttributeResponse from(Attribute a) {
        return new AttributeResponse(a.getId(), a.getCode(), a.getName(), a.getSortOrder());
    }
}