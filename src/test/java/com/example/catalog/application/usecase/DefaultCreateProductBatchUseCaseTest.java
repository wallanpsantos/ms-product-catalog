package com.example.catalog.application.usecase;

import com.example.catalog.UnitTest;
import com.example.catalog.application.port.input.CreateProductUseCase;
import com.example.catalog.application.port.output.ProductCommandGateway;
import com.example.catalog.domain.exception.NotificationException;
import com.example.catalog.domain.product.Product;
import com.example.catalog.domain.validation.Error;
import com.example.catalog.domain.validation.handler.Notification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
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
    @DisplayName("Deve lançar NotificationException se houver erro de validacao no lote")
    void givenInvalidInput_whenCreateBatch_thenThrowNotificationException() {
        // Given
        final var input1 = new CreateProductUseCase.Input("", "D1", "C1", "B1", BigDecimal.TEN, true);

        // When / Then
        assertThatThrownBy(() -> useCase.execute(List.of(input1)))
                .isInstanceOf(NotificationException.class)
                .hasMessageContaining("Erro de validação no item do lote índice: 0");
    }

    @Test
    @DisplayName("Deve lançar NotificationException quando validator acumula erro no item do lote")
    void givenInputThatPassesFailFastButFailsValidator_whenCreateBatch_thenThrowNotificationExceptionWithIndex() {
        // Given
        final var input1 = new CreateProductUseCase.Input("P1", "D1", "C1", "B1", BigDecimal.TEN, true);
        final var input2 = new CreateProductUseCase.Input("P2", "D2", "C2", "B2", BigDecimal.TEN, true);

        try (var mockedProduct = mockStatic(Product.class)) {
            final var productMock1 = mock(Product.class);
            final var productMock2 = mock(Product.class);

            mockedProduct.when(() -> Product.newProduct("P1", "D1", "C1", "B1", BigDecimal.TEN, true))
                    .thenReturn(productMock1);
            mockedProduct.when(() -> Product.newProduct("P2", "D2", "C2", "B2", BigDecimal.TEN, true))
                    .thenReturn(productMock2);

            doNothing().when(productMock1).validate(any());

            doAnswer(invocation -> {
                Notification n = invocation.getArgument(0);
                n.append(new Error("Mocked validation error on P2"));
                return null;
            }).when(productMock2).validate(any());

            // When / Then
            assertThatThrownBy(() -> useCase.execute(List.of(input1, input2)))
                    .isInstanceOf(NotificationException.class)
                    .hasMessageContaining("Erro de validação no item do lote índice: 1");
        }
    }
}