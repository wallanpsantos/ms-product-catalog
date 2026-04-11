package com.example.catalog.infrastructure.adapter.output.persistence;

import com.example.catalog.IntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class ProductJpaRepositoryIntegrationTest {

    @Autowired
    private ProductJpaRepository repository;

    @Test
    @DisplayName("Deve buscar produtos ativos paginados com sucesso no PostgreSQL via Testcontainers")
    void shouldFindAllByActiveTrue() {
        // Given
        ProductJpaEntity activeProduct = new ProductJpaEntity(
                UUID.randomUUID().toString(),
                "Smartphone",
                "Smartphone 5G",
                "Eletrônicos",
                "BrandX",
                new BigDecimal("3000.00"),
                true,
                Instant.now(),
                Instant.now()
        );
        ProductJpaEntity inactiveProduct = new ProductJpaEntity(
                UUID.randomUUID().toString(),
                "Tablet Velho",
                "Tablet antigo",
                "Eletrônicos",
                "BrandY",
                new BigDecimal("500.00"),
                false,
                Instant.now(),
                Instant.now()
        );

        repository.saveAll(List.of(activeProduct, inactiveProduct));

        // When
        Page<ProductJpaEntity> page = repository.findAllByActiveTrue(PageRequest.of(0, 10));

        // Then
        assertThat(page.getContent())
                .hasSize(1)
                .extracting(ProductJpaEntity::getId)
                .containsExactly(activeProduct.getId());
    }

    @Test
    @DisplayName("Deve buscar produtos ativos usando query customizada com sucesso no PostgreSQL via Testcontainers")
    void shouldSearchActiveProducts() {
        // Given
        ProductJpaEntity prod1 = new ProductJpaEntity(
                UUID.randomUUID().toString(),
                "Notebook Gamer",
                "Notebook alta performance",
                "Computadores",
                "BrandZ",
                new BigDecimal("8000.00"),
                true,
                Instant.now(),
                Instant.now()
        );
        ProductJpaEntity prod2 = new ProductJpaEntity(
                UUID.randomUUID().toString(),
                "Teclado Gamer",
                "Teclado mecânico",
                "Acessórios",
                "BrandZ",
                new BigDecimal("300.00"),
                true,
                Instant.now(),
                Instant.now()
        );
        ProductJpaEntity inactiveProd = new ProductJpaEntity(
                UUID.randomUUID().toString(),
                "Mouse Gamer",
                "Mouse ótico",
                "Acessórios",
                "BrandZ",
                new BigDecimal("150.00"),
                false,
                Instant.now(),
                Instant.now()
        );

        repository.saveAll(List.of(prod1, prod2, inactiveProd));

        // When
        List<ProductJpaEntity> results = repository.searchActiveProducts("gamer");

        // Then
        assertThat(results)
                .hasSize(2)
                .extracting(ProductJpaEntity::getId)
                .containsExactlyInAnyOrder(prod1.getId(), prod2.getId());
    }
}