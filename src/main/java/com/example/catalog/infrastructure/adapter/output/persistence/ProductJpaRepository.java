package com.example.catalog.infrastructure.adapter.output.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repositório Spring Data JPA para operações CRUD básicas.
 */
public interface ProductJpaRepository extends JpaRepository<ProductJpaEntity, String> {

    Page<ProductJpaEntity> findAllByActiveTrue(Pageable pageable);

    @Query("SELECT p FROM ProductJpaEntity p WHERE p.active = true AND (" +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.category) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.brand) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<ProductJpaEntity> searchActiveProducts(@Param("query") String query);
}
