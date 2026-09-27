package com.store.common.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Utility to build a safe {@link Pageable} from raw query parameters.
 *
 * <p>
 * Guards against:
 * <ul>
 * <li>negative page numbers</li>
 * <li>oversized page sizes (DoS protection)</li>
 * <li>null / blank sort fields (falls back to id)</li>
 * <li>case-insensitive direction ("asc"/"desc"/"ASC"/"DESC")</li>
 * </ul>
 *
 * <p>
 * Usage in a controller:
 * 
 * <pre>
 * &#64;GetMapping
 * public PageResponse&lt;UserResponse&gt; list(&#64;RequestParam(defaultValue = "0") int page,
 * 		&#64;RequestParam(defaultValue = "20") int size, &#64;RequestParam(defaultValue = "id") String sortBy,
 * 		&#64;RequestParam(defaultValue = "asc") String direction) {
 *
 * 	Pageable pageable = PageRequestBuilder.build(page, size, sortBy, direction);
 * 	return userService.list(pageable);
 * }
 * </pre>
 */
public final class PageRequestBuilder {

	/** Hard cap on page size to prevent abuse. */
	public static final int MAX_PAGE_SIZE = 100;

	/** Default page size when none is provided. */
	public static final int DEFAULT_PAGE_SIZE = 20;

	/** Default sort field when none is provided. */
	public static final String DEFAULT_SORT_FIELD = "id";

	private PageRequestBuilder() {
		// utility class — no instantiation
	}

	/**
	 * Build a {@link Pageable} from raw query parameters with safe defaults.
	 *
	 * @param page      0-based page index (negative values clamped to 0)
	 * @param size      page size (clamped to [1, {@value #MAX_PAGE_SIZE}])
	 * @param sortBy    field name to sort by (null/blank →
	 *                  {@value #DEFAULT_SORT_FIELD})
	 * @param direction "asc" or "desc" (case-insensitive; anything else → asc)
	 * @return a validated {@link Pageable}
	 */
	public static Pageable build(int page, int size, String sortBy, String direction) {
		int safePage = Math.max(page, 0);
		int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
		String safeSort = (sortBy == null || sortBy.isBlank()) ? DEFAULT_SORT_FIELD : sortBy.trim();

		Sort.Direction dir = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;

		return PageRequest.of(safePage, safeSize, Sort.by(dir, safeSort));
	}

	/**
	 * Convenience overload: default sort by {@value #DEFAULT_SORT_FIELD} ascending.
	 */
	public static Pageable build(int page, int size) {
		return build(page, size, DEFAULT_SORT_FIELD, "asc");
	}

	/**
	 * Build a {@link Pageable} with multiple sort fields.
	 *
	 * <p>
	 * Usage:
	 * 
	 * <pre>
	 * PageRequestBuilder.build(0, 20, "createdAt,desc", "email,asc");
	 * </pre>
	 *
	 * @param page      0-based page index
	 * @param size      page size
	 * @param sortSpecs varargs of "field,direction" pairs
	 */
	public static Pageable build(int page, int size, String... sortSpecs) {
		int safePage = Math.max(page, 0);
		int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);

		if (sortSpecs == null || sortSpecs.length == 0) {
			return PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.ASC, DEFAULT_SORT_FIELD));
		}

		List<Sort.Order> orders = new ArrayList<>();
		for (String spec : sortSpecs) {
			if (spec == null || spec.isBlank())
				continue;

			String[] parts = spec.split(",");
			String field = parts[0].trim();
			Sort.Direction dir = (parts.length > 1 && "desc".equalsIgnoreCase(parts[1].trim())) ? Sort.Direction.DESC
					: Sort.Direction.ASC;

			if (!field.isEmpty()) {
				orders.add(new Sort.Order(dir, field));
			}
		}

		if (orders.isEmpty()) {
			orders.add(new Sort.Order(Sort.Direction.ASC, DEFAULT_SORT_FIELD));
		}

		return PageRequest.of(safePage, safeSize, Sort.by(orders));
	}
}