package com.example.catalog.application.usecase;

import com.example.catalog.UnitTest;
import com.example.catalog.application.port.input.DeactivateProductUseCase;
import com.example.catalog.application.port.output.ProductCommandGateway;
import com.example.catalog.domain.exception.NotFoundException;
import com.example.catalog.domain.product.Product;
import com.example.catalog.domain.product.ProductID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
class DefaultDeactivateProductUseCaseTest {

    @Mock
    private ProductCommandGateway productGateway;

    @InjectMocks
    private DefaultDeactivateProductUseCase useCase;

    @Test
    @DisplayName("Deve desativar um produto com sucesso")
    void givenAValidId_whenCallDeactivateProduct_thenShouldDeactivate() {
        // Given
        final var product = Product.newProduct("Name", "Desc", "Cat", "Brand", BigDecimal.TEN, true);
        final var id = product.getId().getValue();
        final var input = new DeactivateProductUseCase.Input(id);

        when(productGateway.findById(any())).thenReturn(Optional.of(product));
        when(productGateway.update(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        final var output = useCase.execute(input);

        // Then
        assertThat(output).isNotNull();
        assertThat(product.isActive()).isFalse();

        verify(productGateway).findById(any(ProductID.class));
        verify(productGateway).update(product);
    }

    @Test
    @DisplayName("Deve lancar NotFoundException ao tentar desativar um produto inexistente")
    void givenAnInvalidId_whenCallDeactivateProduct_thenShouldThrowNotFoundException() {
        // Given
        final var id = "invalid-id";
        final var input = new DeactivateProductUseCase.Input(id);

        when(productGateway.findById(any())).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Product with id invalid-id was not found");

        verify(productGateway).findById(any(ProductID.class));
    }
}