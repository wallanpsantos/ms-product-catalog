package com.example.catalog.infrastructure.adapter.output.gateway;

import com.example.catalog.IntegrationTest;
import com.example.catalog.domain.product.Product;
import com.example.catalog.domain.product.ProductID;
import com.example.catalog.infrastructure.adapter.output.persistence.ProductJpaEntityFixture;
import com.example.catalog.infrastructure.adapter.output.persistence.ProductJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class ProductJpaCommandAdapterIntegrationTest {

    @Autowired
    private ProductJpaCommandAdapter adapter;

    @Autowired
    private ProductJpaRepository repository;

    @Test
    @DisplayName("Deve criar um produto com sucesso traduzindo de Dominio para JpaEntity")
    void shouldCreateProduct() {
        // Given
        final var product = Product.newProduct("Notebook", "Desc", "Cat", "Brand", BigDecimal.TEN, true);

        // When
        final var saved = adapter.create(product);

        // Then
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isEqualTo(product.getId());

        final var entity = repository.findById(product.getId().getValue()).orElseThrow();
        assertThat(entity.getName()).isEqualTo("Notebook");
    }

    @Test
    @DisplayName("Deve atualizar um produto com sucesso")
    void shouldUpdateProduct() {
        // Given
        final var id = UUID.randomUUID().toString();
        repository.save(ProductJpaEntityFixture.active(id, "Old Name"));

        final var product = adapter.findById(ProductID.from(id)).orElseThrow();
        product.update("New Name", "Desc", "Cat", "Brand", BigDecimal.TEN, true);

        // When
        final var updated = adapter.update(product);

        // Then
        assertThat(updated.getName()).isEqualTo("New Name");

        final var entity = repository.findById(id).orElseThrow();
        assertThat(entity.getName()).isEqualTo("New Name");
    }

    @Test
    @DisplayName("Deve criar produtos em lote com sucesso")
    void shouldCreateAllProducts() {
        // Given
        final var initialCount = repository.count();
        final var p1 = Product.newProduct("P1", "D", "C", "B", BigDecimal.TEN, true);
        final var p2 = Product.newProduct("P2", "D", "C", "B", BigDecimal.TEN, true);

        // When
        final var saved = adapter.createAll(List.of(p1, p2));

        // Then
        assertThat(saved).hasSize(2);
        assertThat(repository.count()).isEqualTo(initialCount + 2);
    }

    @Test
    @DisplayName("Deve buscar todos os produtos pelos IDs solicitados")
    void shouldFindAllByIds() {
        // Given
        final var id1 = UUID.randomUUID().toString();
        final var id2 = UUID.randomUUID().toString();
        repository.save(ProductJpaEntityFixture.active(id1, "P1"));
        repository.save(ProductJpaEntityFixture.active(id2, "P2"));

        // When
        final var products = adapter.findAllById(List.of(ProductID.from(id1), ProductID.from(id2)));

        // Then
        assertThat(products).hasSize(2);
        assertThat(products).extracting(Product::getName).containsExactlyInAnyOrder("P1", "P2");
    }
}
