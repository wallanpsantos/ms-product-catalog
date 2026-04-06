package com.example.catalog.application.usecase;

import com.example.catalog.IntegrationTest;
import com.example.catalog.application.port.input.CreateProductBatchUseCase;
import com.example.catalog.application.port.input.CreateProductUseCase;
import com.example.catalog.domain.exception.DomainException;
import com.example.catalog.infrastructure.adapter.output.persistence.ProductJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@IntegrationTest
class DefaultCreateProductBatchUseCaseIntegrationTest {

    @Autowired
    private CreateProductBatchUseCase useCase;

    @Autowired
    private ProductJpaRepository repository;

    @Test
    @DisplayName("Deve criar produtos em lote com sucesso e persistir no banco")
    void shouldCreateProductBatchAndPersist() {
        // Given
        final var initialCount = repository.count();
        final var input1 = new CreateProductUseCase.Input("P1", "D1", "C1", "B1", BigDecimal.TEN, true);
        final var input2 = new CreateProductUseCase.Input("P2", "D2", "C2", "B2", BigDecimal.TEN, true);

        // When
        final var outputs = useCase.execute(List.of(input1, input2));

        // Then
        assertThat(outputs).hasSize(2);
        assertThat(repository.count()).isEqualTo(initialCount + 2);
    }

    @Test
    @DisplayName("Deve lançar exceção de validação e não persistir NENHUM item se houver falha no meio do lote")
    void shouldRollbackAllIfOneFailsValidation() {
        // Given
        final var initialCount = repository.count();
        final var valid = new CreateProductUseCase.Input("Valid Product", "D", "C", "B", BigDecimal.TEN, true);
        // "" triggers fail-fast in validator
        final var invalid = new CreateProductUseCase.Input("", "D", "C", "B", BigDecimal.TEN, true);

        // When / Then
        assertThatThrownBy(() -> useCase.execute(List.of(valid, invalid)))
                .isInstanceOf(DomainException.class);

        // Verify transaction boundaries / no partial commit
        assertThat(repository.count()).isEqualTo(initialCount);
    }
}
