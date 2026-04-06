package com.example.catalog.application.usecase;

import com.example.catalog.IntegrationTest;
import com.example.catalog.application.port.input.DeactivateProductUseCase;
import com.example.catalog.domain.exception.NotFoundException;
import com.example.catalog.infrastructure.adapter.output.persistence.ProductJpaEntityFixture;
import com.example.catalog.infrastructure.adapter.output.persistence.ProductJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@IntegrationTest
class DefaultDeactivateProductUseCaseIntegrationTest {

    @Autowired
    private DeactivateProductUseCase useCase;

    @Autowired
    private ProductJpaRepository repository;

    @Test
    @Transactional
    @DisplayName("Deve desativar produto com sucesso e persistir mudança")
    void shouldDeactivateProductAndPersist() {
        // Given
        final var id = UUID.randomUUID().toString();
        repository.save(ProductJpaEntityFixture.active(id, "Test"));

        final var input = new DeactivateProductUseCase.Input(id);

        // When
        useCase.execute(input);

        // Then
        final var savedEntity = repository.findById(id).orElseThrow();
        assertThat(savedEntity.getActive()).isFalse();
    }

    @Test
    @Transactional
    @DisplayName("Deve lançar NotFoundException quando produto não existe e garantir que nada muda no banco")
    void shouldThrowNotFoundExceptionAndRollback() {
        // Given
        final var initialCount = repository.count();
        final var input = new DeactivateProductUseCase.Input(UUID.randomUUID().toString());

        // When / Then
        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Product with id");

        // Verify transaction boundaries / no unexpected changes
        assertThat(repository.count()).isEqualTo(initialCount);
    }
}
