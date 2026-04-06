package com.example.catalog.application.usecase;

import com.example.catalog.UnitTest;
import com.example.catalog.application.port.input.CreateProductUseCase;
import com.example.catalog.application.port.output.ProductCommandGateway;
import com.example.catalog.domain.exception.DomainException;
import com.example.catalog.domain.exception.NotificationException;
import com.example.catalog.domain.product.Product;
import com.example.catalog.domain.validation.Error;
import com.example.catalog.domain.validation.handler.Notification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
class DefaultCreateProductUseCaseTest {

    @InjectMocks
    private DefaultCreateProductUseCase useCase;

    @Mock
    private ProductCommandGateway productGateway;

    @Test
    @DisplayName("Deve retornar Output com ID ao criar produto válido")
    void givenValidInput_whenExecute_thenReturnOutputWithId() {
        // Given
        final var input = new CreateProductUseCase.Input(
                "Smartphone X", "Um smartphone potente", "Electronics",
                "BrandZ", new BigDecimal("1999.99"), true
        );

        when(productGateway.create(any(Product.class))).thenAnswer(invocation -> {
            return invocation.getArgument(0); // As Product generates ID on creation
        });

        // When
        final var output = useCase.execute(input);

        // Then
        assertThat(output).isNotNull();
        assertThat(output.id()).isNotBlank();

        verify(productGateway, times(1)).create(any(Product.class));
    }

    @Test
    @DisplayName("Deve lançar DomainException no construtor fail-fast ao criar produto com valores inválidos nulos")
    void givenInvalidInputWithNulls_whenExecute_thenThrowDomainException() {
        // Given
        final var input = new CreateProductUseCase.Input(
                null, "Um smartphone potente", "Electronics",
                "BrandZ", new BigDecimal("1999.99"), true
        );

        // When/Then
        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("'name' should not be empty or null");

        verify(productGateway, times(0)).create(any());
    }

    @Test
    @DisplayName("Deve lançar NotificationException via validator quando produto tiver regras de negócio violadas")
    void givenInvalidInputWithBusinessRulesViolated_whenExecute_thenThrowNotificationException() {
        // Given
        final var input = new CreateProductUseCase.Input(
                "A", // Muito curto, dependendo da regra
                "Um smartphone potente", "Electronics",
                "BrandZ", new BigDecimal("10.00"), // Preço valido
                true
        );

        // Quando a regra é checada pelo validator e não no fail-fast
        // Esse teste valida que a NotificationException é lançada.

        try (var mockedProduct = mockStatic(Product.class)) {
            final var productMock = mock(Product.class);
            mockedProduct.when(() -> Product.newProduct(any(), any(), any(), any(), any(), anyBoolean()))
                    .thenReturn(productMock);

            doAnswer(invocation -> {
                Notification n = invocation.getArgument(0);
                n.append(new Error("Mocked validation error"));
                return null;
            }).when(productMock).validate(any());

            // When/Then
            assertThatThrownBy(() -> useCase.execute(input))
                    .isInstanceOf(NotificationException.class)
                    .hasMessageContaining("Não foi possível criar o Agregado de Produto");

            verify(productGateway, times(0)).create(any());
        }
    }

    @Test
    @DisplayName("Deve defaultar active=true quando input.active() for null")
    void givenValidInputWithNullActive_whenExecute_thenDefaultActiveToTrue() {
        // Given
        final var input = new CreateProductUseCase.Input(
                "Smartphone X", "Um smartphone potente", "Electronics",
                "BrandZ", new BigDecimal("1999.99"), null
        );

        when(productGateway.create(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        useCase.execute(input);

        // Then
        verify(productGateway, times(1)).create(org.mockito.ArgumentMatchers.argThat(product -> {
            assertThat(product.isActive()).isTrue();
            return true;
        }));
    }
}
