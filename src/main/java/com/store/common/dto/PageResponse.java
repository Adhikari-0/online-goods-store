package com.store.common.dto;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

/**
 * Generic wrapper for paginated REST API responses.
 *
 * <p>
 * Spring Data's {@link Page} interface is not stable for JSON serialization
 * across Spring Boot versions (Spring Boot 3 removed the built-in PageImpl
 * serializer). This record gives you a stable, explicit API contract.
 *
 * <p>
 * Example JSON output:
 * 
 * <pre>
 * {
 *   "content": [...],
 *   "page": 0,
 *   "size": 20,
 *   "totalElements": 42,
 *   "totalPages": 3,
 *   "first": true,
 *   "last": false,
 *   "hasNext": true,
 *   "hasPrevious": false
 * }
 * </pre>
 *
 * @param <T> the DTO type of each item in the page
 */
public record PageResponse<T>(List<T> content, int page, int size, long totalElements, int totalPages, boolean first,
		boolean last, boolean hasNext, boolean hasPrevious) {

	/**
	 * Build directly from a Spring Data {@link Page} of already-mapped DTOs.
	 */
	public static <T> PageResponse<T> of(Page<T> page) {
		return new PageResponse<>(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements(),
				page.getTotalPages(), page.isFirst(), page.isLast(), page.hasNext(), page.hasPrevious());
	}

	/**
	 * Build from a {@link Page} of entities by mapping each entity to a DTO.
	 *
	 * <p>
	 * Usage:
	 * 
	 * <pre>
	 * return PageResponse.of(userRepository.findAll(pageable), this::toResponse);
	 * </pre>
	 */
	public static <E, D> PageResponse<D> of(Page<E> page, Function<E, D> mapper) {
		List<D> mapped = page.getContent().stream().map(mapper).toList();

		return new PageResponse<>(mapped, page.getNumber(), page.getSize(), page.getTotalElements(),
				page.getTotalPages(), page.isFirst(), page.isLast(), page.hasNext(), page.hasPrevious());
	}

	/**
	 * Build an empty page (useful for fallback returns or early exits).
	 */
	public static <T> PageResponse<T> empty(int page, int size) {
		return new PageResponse<>(List.of(), page, size, 0L, 0, true, true, false, false);
	}
}