package com.example.catalog.application.usecase;

import com.example.catalog.UnitTest;
import com.example.catalog.application.port.input.UpdateProductUseCase;
import com.example.catalog.application.port.output.ProductCommandGateway;
import com.example.catalog.domain.exception.DomainException;
import com.example.catalog.domain.exception.NotFoundException;
import com.example.catalog.domain.exception.NotificationException;
import com.example.catalog.domain.product.Product;
import com.example.catalog.domain.product.ProductID;
import com.example.catalog.domain.validation.Error;
import com.example.catalog.domain.validation.handler.Notification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
class DefaultUpdateProductUseCaseTest {

    @InjectMocks
    private DefaultUpdateProductUseCase useCase;

    @Mock
    private ProductCommandGateway productGateway;

    @Test
    @DisplayName("Deve atualizar produto com sucesso")
    void givenValidInput_whenExecute_thenReturnOutputWithId() {
        // Given
        final var expectedId = UUID.randomUUID().toString();
        final var product = Product.newProduct("Nome Antigo", "Desc Antiga", "Cat Antiga", "Brand", new BigDecimal("10.0"), true);

        final var input = new UpdateProductUseCase.Input(
                expectedId, "Notebook Pro", "Notebook de alta performance",
                "Electronics", "BrandX", new BigDecimal("4999.99"), true
        );

        when(productGateway.findById(any(ProductID.class))).thenReturn(Optional.of(product));
        when(productGateway.update(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        final var output = useCase.execute(input);

        // Then
        assertThat(output).isNotNull();
        assertThat(output.id()).isEqualTo(product.getId().getValue());

        verify(productGateway, times(1)).findById(ProductID.from(expectedId));
        verify(productGateway, times(1)).update(any(Product.class));
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando produto não existir")
    void givenInvalidId_whenExecute_thenThrowNotFoundException() {
        // Given
        final var expectedId = UUID.randomUUID().toString();
        final var input = new UpdateProductUseCase.Input(
                expectedId, "Notebook Pro", "Notebook de alta performance",
                "Electronics", "BrandX", new BigDecimal("4999.99"), true
        );

        when(productGateway.findById(any(ProductID.class))).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Product with id %s was not found", expectedId);

        verify(productGateway, times(1)).findById(ProductID.from(expectedId));
        verify(productGateway, times(0)).update(any());
    }

    @Test
    @DisplayName("Deve lançar DomainException no construtor fail-fast ao atualizar com valores inválidos nulos")
    void givenInvalidInputWithNulls_whenExecute_thenThrowDomainException() {
        // Given
        final var expectedId = UUID.randomUUID().toString();
        final var product = Product.newProduct("Nome Antigo", "Desc Antiga", "Cat Antiga", "Brand", new BigDecimal("10.0"), true);

        final var input = new UpdateProductUseCase.Input(
                expectedId, null, "Notebook de alta performance",
                "Electronics", "BrandX", new BigDecimal("4999.99"), true
        );

        when(productGateway.findById(any(ProductID.class))).thenReturn(Optional.of(product));

        // When/Then
        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("'name' should not be empty or null");

        verify(productGateway, times(1)).findById(ProductID.from(expectedId));
        verify(productGateway, times(0)).update(any());
    }

    @Test
    @DisplayName("Deve lançar NotificationException via validator quando produto tiver regras de negócio violadas na atualização")
    void givenInvalidInputWithBusinessRulesViolated_whenExecute_thenThrowNotificationException() {
        // Given
        final var expectedId = UUID.randomUUID().toString();

        // Usamos um mock para evitar o fail-fast e testar o NotificationPattern
        final var productMock = mock(Product.class);

        // Simulando a falha de validação via Notification Pattern
        doAnswer(invocation -> {
            Notification notification = invocation.getArgument(0);
            notification.append(new Error("Preço não pode ser negativo"));
            return null;
        }).when(productMock).validate(any());

        final var input = new UpdateProductUseCase.Input(
                expectedId, "Notebook Pro", "Notebook de alta performance",
                "Electronics", "BrandX", new BigDecimal("-100.00"), true
        );

        when(productGateway.findById(any(ProductID.class))).thenReturn(Optional.of(productMock));

        // When/Then
        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(NotificationException.class)
                .hasMessageContaining("Não foi possível atualizar o Agregado de Produto");

        verify(productGateway, times(1)).findById(ProductID.from(expectedId));
        verify(productGateway, times(0)).update(any());
    }
}
