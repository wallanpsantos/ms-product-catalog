package com.example.catalog.application.usecase;

import com.example.catalog.IntegrationTest;
import com.example.catalog.application.port.input.ListActiveProductsUseCase;
import com.example.catalog.domain.pagination.SearchQuery;
import com.example.catalog.infrastructure.adapter.output.persistence.ProductJpaEntityFixture;
import com.example.catalog.infrastructure.adapter.output.persistence.ProductJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class DefaultListActiveProductsUseCaseIntegrationTest {

    @Autowired
    private ListActiveProductsUseCase useCase;

    @Autowired
    private ProductJpaRepository repository;

    @Test
    @DisplayName("Deve retornar apenas produtos ativos com paginacao correta")
    void shouldReturnOnlyActiveProductsWithPagination() {
        // Given
        repository.deleteAll();
        repository.save(ProductJpaEntityFixture.active(UUID.randomUUID().toString(), "Teclado"));
        repository.save(ProductJpaEntityFixture.active(UUID.randomUUID().toString(), "Mouse"));
        repository.save(ProductJpaEntityFixture.inactive(UUID.randomUUID().toString()));

        final var query = new SearchQuery(0, 10, "", "name", "asc");

        // When
        final var output = useCase.execute(query);

        // Then
        assertThat(output.items()).hasSize(2);
        assertThat(output.total()).isEqualTo(2);
        assertThat(output.items())
                .extracting(ListActiveProductsUseCase.Output::name)
                .containsExactly("Mouse", "Teclado"); // sorted by name asc
    }

    @Test
    @DisplayName("Deve retornar vazio quando banco esta vazio ou sem ativos")
    void shouldReturnEmptyWhenNoActiveProducts() {
        // Given
        repository.deleteAll();
        repository.save(ProductJpaEntityFixture.inactive(UUID.randomUUID().toString()));

        final var query = new SearchQuery(0, 10, "", "name", "asc");

        // When
        final var output = useCase.execute(query);

        // Then
        assertThat(output.items()).isEmpty();
        assertThat(output.total()).isZero();
    }
}
