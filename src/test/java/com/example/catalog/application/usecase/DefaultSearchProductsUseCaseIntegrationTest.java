package com.example.catalog.application.usecase;

import com.example.catalog.IntegrationTest;
import com.example.catalog.application.port.input.SearchProductsUseCase;
import com.example.catalog.infrastructure.adapter.output.persistence.ProductJpaEntityFixture;
import com.example.catalog.infrastructure.adapter.output.persistence.ProductJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class DefaultSearchProductsUseCaseIntegrationTest {

    @Autowired
    private SearchProductsUseCase useCase;

    @Autowired
    private ProductJpaRepository repository;

    @Test
    @DisplayName("Deve retornar produtos pesquisados pelo termo correto ignorando inativos")
    void shouldSearchProductsAndIgnoreInactive() {
        // Given
        repository.deleteAll();
        repository.save(ProductJpaEntityFixture.active(UUID.randomUUID().toString(), "Teclado Redragon"));
        repository.save(ProductJpaEntityFixture.active(UUID.randomUUID().toString(), "Mouse Razer"));
        repository.save(ProductJpaEntityFixture.inactive(UUID.randomUUID().toString(), "Teclado Multilaser"));

        final var input = new SearchProductsUseCase.Input("Teclado");

        // When
        final var outputs = useCase.execute(input);

        // Then
        assertThat(outputs).hasSize(1);
        assertThat(outputs.getFirst().name()).isEqualTo("Teclado Redragon");
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando pesquisa não bate com ativos")
    void shouldReturnEmptyWhenSearchDoesNotMatch() {
        // Given
        repository.deleteAll();
        repository.save(ProductJpaEntityFixture.active(UUID.randomUUID().toString(), "Monitor LG"));

        final var input = new SearchProductsUseCase.Input("Teclado");

        // When
        final var outputs = useCase.execute(input);

        // Then
        assertThat(outputs).isEmpty();
    }
}
