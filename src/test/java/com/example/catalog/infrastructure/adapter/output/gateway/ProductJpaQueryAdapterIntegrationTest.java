package com.example.catalog.infrastructure.adapter.output.gateway;

import com.example.catalog.IntegrationTest;
import com.example.catalog.application.port.output.dto.ProductSummary;
import com.example.catalog.domain.pagination.SearchQuery;
import com.example.catalog.domain.product.ProductID;
import com.example.catalog.infrastructure.adapter.output.persistence.ProductJpaEntityFixture;
import com.example.catalog.infrastructure.adapter.output.persistence.ProductJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class ProductJpaQueryAdapterIntegrationTest {

    @Autowired
    private ProductJpaQueryAdapter adapter;

    @Autowired
    private ProductJpaRepository repository;

    @Test
    @DisplayName("Deve buscar o sumario de um produto pelo ID")
    void shouldFindSummaryById() {
        // Given
        final var id = UUID.randomUUID().toString();
        repository.save(ProductJpaEntityFixture.active(id, "Test Product"));

        // When
        final var summary = adapter.findSummaryById(ProductID.from(id));

        // Then
        assertThat(summary).isPresent();
        assertThat(summary.get().name()).isEqualTo("Test Product");
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar por ID inexistente")
    void shouldReturnEmptyWhenFindSummaryByIdNotFound() {
        // Given
        final var id = UUID.randomUUID().toString();

        // When
        final var summary = adapter.findSummaryById(ProductID.from(id));

        // Then
        assertThat(summary).isEmpty();
    }

    @Test
    @DisplayName("Deve listar todos os produtos ativos de forma paginada")
    void shouldFindAllActiveSummary() {
        // Given
        repository.deleteAll();
        repository.save(ProductJpaEntityFixture.active(UUID.randomUUID().toString(), "P1"));
        repository.save(ProductJpaEntityFixture.active(UUID.randomUUID().toString(), "P2"));
        repository.save(ProductJpaEntityFixture.inactive(UUID.randomUUID().toString(), "P3"));

        final var query = new SearchQuery(0, 10, "", "name", "desc");

        // When
        final var page = adapter.findAllActiveSummary(query);

        // Then
        assertThat(page.total()).isEqualTo(2);
        assertThat(page.items()).hasSize(2);
        assertThat(page.items()).extracting(ProductSummary::name).containsExactly("P2", "P1"); // desc
    }

    @Test
    @DisplayName("Deve pesquisar produtos sumariados filtrando termo de busca e inativos")
    void shouldSearchProductsSummary() {
        // Given
        repository.deleteAll();
        repository.save(ProductJpaEntityFixture.active(UUID.randomUUID().toString(), "Mouse Razer"));
        repository.save(ProductJpaEntityFixture.active(UUID.randomUUID().toString(), "Teclado Razer"));
        repository.save(ProductJpaEntityFixture.active(UUID.randomUUID().toString(), "Teclado Logitech"));
        repository.save(ProductJpaEntityFixture.inactive(UUID.randomUUID().toString(), "Headset Razer"));

        // When
        final var results = adapter.searchProductsSummary("Razer");

        // Then
        assertThat(results).hasSize(2);
        assertThat(results).extracting(ProductSummary::name).containsExactlyInAnyOrder("Mouse Razer", "Teclado Razer");
    }
}
