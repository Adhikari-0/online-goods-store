package com.store.catalog;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySlug(String slug);

    boolean existsBySlug(String slug);

    Page<Product> findAllByStatus(ProductStatusEnum status, Pageable pageable);

    Page<Product> findAllByVendorId(Long vendorId, Pageable pageable);

    Page<Product> findAllByCategoryIdAndStatus(Long categoryId, ProductStatusEnum status, Pageable pageable);

    Page<Product> findAllByBrandIdAndStatus(Long brandId, ProductStatusEnum status, Pageable pageable);

    List<Product> findAllByFeaturedTrueAndStatus(ProductStatusEnum status);

    @Query("""
        SELECT DISTINCT p FROM Product p
        LEFT JOIN FETCH p.images
        LEFT JOIN FETCH p.variants
        WHERE p.id = :id
    """)
    Optional<Product> findByIdWithDetails(@Param("id") Long id);

    @Query("""
        SELECT DISTINCT p FROM Product p
        LEFT JOIN FETCH p.images
        WHERE p.slug = :slug
    """)
    Optional<Product> findBySlugWithImages(@Param("slug") String slug);

    @Query("""
        SELECT p FROM Product p
        WHERE p.status = 'ACTIVE'
          AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(p.shortDescription) LIKE LOWER(CONCAT('%', :keyword, '%')))
    """)
    Page<Product> search(@Param("keyword") String keyword, Pageable pageable);
}