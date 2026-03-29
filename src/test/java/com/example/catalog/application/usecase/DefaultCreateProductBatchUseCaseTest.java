package com.example.catalog.application.usecase;

import com.example.catalog.UnitTest;
import com.example.catalog.application.port.input.CreateProductUseCase;
import com.example.catalog.application.port.output.ProductCommandGateway;
import com.example.catalog.domain.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
class DefaultCreateProductBatchUseCaseTest {

    @Mock
    private ProductCommandGateway productGateway;

    @InjectMocks
    private DefaultCreateProductBatchUseCase useCase;

    @Test
    @DisplayName("Deve criar produtos em lote com sucesso")
    void givenValidInputs_whenCallCreateBatch_thenCreateProducts() {
        // Given
        final var input1 = new CreateProductUseCase.Input("P1", "D1", "C1", "B1", BigDecimal.TEN, true);
        final var input2 = new CreateProductUseCase.Input("P2", "D2", "C2", "B2", BigDecimal.TEN, null);

        when(productGateway.createAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        final var outputs = useCase.execute(List.of(input1, input2));

        // Then
        assertThat(outputs).hasSize(2);
        verify(productGateway).createAll(anyList());
    }

    @Test
    @DisplayName("Deve lancar DomainException se houver erro de validacao (Fail Fast)")
    void givenInvalidData_whenCallCreateBatch_thenThrowDomainException() {
        // Given
        // Invalid name (empty)
        final var input1 = new CreateProductUseCase.Input("", "D1", "C1", "B1", BigDecimal.TEN, true);

        // When / Then
        assertThatThrownBy(() -> useCase.execute(List.of(input1)))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("'name' should not be empty or null");
    }
}