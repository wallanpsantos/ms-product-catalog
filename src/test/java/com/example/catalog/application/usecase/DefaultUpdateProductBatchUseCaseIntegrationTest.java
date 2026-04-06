package com.example.catalog.application.usecase;

import com.example.catalog.IntegrationTest;
import com.example.catalog.application.port.input.UpdateProductBatchUseCase;
import com.example.catalog.application.port.input.UpdateProductUseCase;
import com.example.catalog.domain.exception.DomainException;
import com.example.catalog.infrastructure.adapter.output.persistence.ProductJpaEntityFixture;
import com.example.catalog.infrastructure.adapter.output.persistence.ProductJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@IntegrationTest
class DefaultUpdateProductBatchUseCaseIntegrationTest {

    @Autowired
    private UpdateProductBatchUseCase useCase;

    @Autowired
    private ProductJpaRepository repository;

    @Test
    @DisplayName("Deve atualizar produtos em lote com sucesso e persistir mudanças")
    void shouldUpdateProductBatchAndPersist() {
        // Given
        final var id1 = UUID.randomUUID().toString();
        final var id2 = UUID.randomUUID().toString();

        repository.save(ProductJpaEntityFixture.active(id1, "Old P1"));
        repository.save(ProductJpaEntityFixture.active(id2, "Old P2"));

        final var input1 = new UpdateProductUseCase.Input(id1, "New P1", "D1", "C1", "B1", BigDecimal.TEN, true);
        final var input2 = new UpdateProductUseCase.Input(id2, "New P2", "D2", "C2", "B2", BigDecimal.TEN, true);

        // When
        useCase.execute(List.of(input1, input2));

        // Then
        final var entity1 = repository.findById(id1).orElseThrow();
        final var entity2 = repository.findById(id2).orElseThrow();

        assertThat(entity1.getName()).isEqualTo("New P1");
        assertThat(entity2.getName()).isEqualTo("New P2");
    }

    @Test
    @DisplayName("Deve dar rollback em tudo se um item do lote falhar validação")
    void shouldRollbackAllIfOneFailsValidation() {
        // Given
        final var id1 = UUID.randomUUID().toString();
        final var id2 = UUID.randomUUID().toString();

        repository.save(ProductJpaEntityFixture.active(id1, "Old P1"));
        repository.save(ProductJpaEntityFixture.active(id2, "Old P2"));

        final var valid = new UpdateProductUseCase.Input(id1, "New P1", "D", "C", "B", BigDecimal.TEN, true);
        final var invalid = new UpdateProductUseCase.Input(id2, "", "D", "C", "B", BigDecimal.TEN, true);

        // When / Then
        assertThatThrownBy(() -> useCase.execute(List.of(valid, invalid)))
                .isInstanceOf(DomainException.class);

        // Verify transaction boundaries / old states are preserved
        final var entity1 = repository.findById(id1).orElseThrow();
        final var entity2 = repository.findById(id2).orElseThrow();

        assertThat(entity1.getName()).isEqualTo("Old P1"); // Rollback happened
        assertThat(entity2.getName()).isEqualTo("Old P2");
    }
}
