package com.store.catalog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findBySlug(String slug);

    boolean existsBySlug(String slug);

    List<Category> findAllByActiveTrue();

    List<Category> findAllByParentIsNullAndActiveTrueOrderBySortOrderAsc();

    List<Category> findAllByParentIdAndActiveTrueOrderBySortOrderAsc(Long parentId);

    @Query("""
        SELECT DISTINCT c FROM Category c
        LEFT JOIN FETCH c.children
        WHERE c.parent IS NULL
    """)
    List<Category> findRootCategoriesWithChildren();

    @Query("""
        SELECT c FROM Category c
        WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    List<Category> search(@Param("keyword") String keyword);
}