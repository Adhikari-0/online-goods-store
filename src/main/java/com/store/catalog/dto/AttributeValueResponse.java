package com.store.catalog.dto;

import com.store.catalog.AttributeValue;

public record AttributeValueResponse(
    Long id,
    Long attributeId,
    String value,
    Integer sortOrder
) {
    public static AttributeValueResponse from(AttributeValue av) {
        return new AttributeValueResponse(
            av.getId(),
            av.getAttribute() != null ? av.getAttribute().getId() : null,
            av.getValue(),
            av.getSortOrder()
        );
    }
}