package com.example.catalog.application.usecase;

import com.example.catalog.UnitTest;
import com.example.catalog.application.port.input.UpdateProductUseCase;
import com.example.catalog.application.port.output.ProductCommandGateway;
import com.example.catalog.domain.exception.DomainException;
import com.example.catalog.domain.exception.NotFoundException;
import com.example.catalog.domain.product.Product;
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
class DefaultUpdateProductBatchUseCaseTest {

    @Mock
    private ProductCommandGateway productGateway;

    @InjectMocks
    private DefaultUpdateProductBatchUseCase useCase;

    @Test
    @DisplayName("Deve atualizar produtos em lote com sucesso")
    void givenValidInputs_whenCallUpdateBatch_thenUpdateProducts() {
        // Given
        final var product1 = Product.newProduct("P1", "D1", "C1", "B1", BigDecimal.TEN, true);
        final var product2 = Product.newProduct("P2", "D2", "C2", "B2", BigDecimal.TEN, true);

        final var input1 = new UpdateProductUseCase.Input(product1.getId().getValue(), "P1_Updated", "D1_U", "C1", "B1", BigDecimal.TEN, true);
        final var input2 = new UpdateProductUseCase.Input(product2.getId().getValue(), "P2_Updated", "D2_U", "C2", "B2", BigDecimal.TEN, true);

        when(productGateway.findAllById(anyList())).thenReturn(List.of(product1, product2));
        when(productGateway.updateAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        final var outputs = useCase.execute(List.of(input1, input2));

        // Then
        assertThat(outputs).hasSize(2);
        assertThat(outputs).extracting(UpdateProductUseCase.Output::id)
                .containsExactlyInAnyOrder(product1.getId().getValue(), product2.getId().getValue());

        verify(productGateway).findAllById(anyList());
        verify(productGateway).updateAll(anyList());
    }

    @Test
    @DisplayName("Deve lancar NotFoundException se algum produto nao for encontrado")
    void givenNonExistentId_whenCallUpdateBatch_thenThrowNotFoundException() {
        // Given
        final var product1 = Product.newProduct("P1", "D1", "C1", "B1", BigDecimal.TEN, true);

        final var input1 = new UpdateProductUseCase.Input(product1.getId().getValue(), "P1_Updated", "D1_U", "C1", "B1", BigDecimal.TEN, true);
        final var input2 = new UpdateProductUseCase.Input("id-inexistente", "P2_Updated", "D2_U", "C2", "B2", BigDecimal.TEN, true);

        when(productGateway.findAllById(anyList())).thenReturn(List.of(product1));

        // When / Then
        assertThatThrownBy(() -> useCase.execute(List.of(input1, input2)))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Product with id id-inexistente was not found");
    }

    @Test
    @DisplayName("Deve lancar DomainException se houver erro de validacao (Fail Fast)")
    void givenInvalidData_whenCallUpdateBatch_thenThrowDomainException() {
        // Given
        final var product1 = Product.newProduct("P1", "D1", "C1", "B1", BigDecimal.TEN, true);

        // Invalid name (empty)
        final var input1 = new UpdateProductUseCase.Input(product1.getId().getValue(), "", "D1_U", "C1", "B1", BigDecimal.TEN, true);

        when(productGateway.findAllById(anyList())).thenReturn(List.of(product1));

        // When / Then
        assertThatThrownBy(() -> useCase.execute(List.of(input1)))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("'name' should not be empty or null");
    }
}