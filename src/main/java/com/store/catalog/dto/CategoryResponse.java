package com.store.catalog.dto;

import com.store.catalog.Category;

import java.time.Instant;
import java.util.List;

public record CategoryResponse(
    Long id,
    String slug,
    String name,
    String description,
    Long parentId,
    Integer sortOrder,
    boolean active,
    String imageUrl,
    List<CategoryResponse> children,
    Instant createdAt
) {
    public static CategoryResponse from(Category c, boolean withChildren) {
        return new CategoryResponse(
            c.getId(),
            c.getSlug(),
            c.getName(),
            c.getDescription(),
            c.getParent() != null ? c.getParent().getId() : null,
            c.getSortOrder(),
            c.isActive(),
            c.getImageUrl(),
            withChildren && c.getChildren() != null
                ? c.getChildren().stream()
                    .map(child -> from(child, false))
                    .toList()
                : List.of(),
            c.getCreatedAt()
        );
    }
}