package com.example.catalog.application.usecase;

import com.example.catalog.IntegrationTest;
import com.example.catalog.application.port.input.CreateProductUseCase;
import com.example.catalog.application.port.input.UpdateProductUseCase;
import com.example.catalog.domain.exception.NotFoundException;
import com.example.catalog.domain.exception.NotificationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@IntegrationTest
class DefaultUpdateProductUseCaseIntegrationTest {

    @Autowired
    private UpdateProductUseCase updateProductUseCase;

    @Autowired
    private CreateProductUseCase createProductUseCase;

    @Test
    @DisplayName("Deve atualizar produto com sucesso")
    void shouldUpdateProductSuccessfully() {
        // Given
        final var createInput = new CreateProductUseCase.Input(
                "Old Name", "Old Desc", "Old Cat", "Old Brand", BigDecimal.TEN, true
        );
        final var createdId = createProductUseCase.execute(createInput).id();

        final var updateInput = new UpdateProductUseCase.Input(
                createdId,
                "New Name", "New Desc", "New Cat", "New Brand", BigDecimal.valueOf(20.0), false
        );

        // When
        final var output = updateProductUseCase.execute(updateInput);

        // Then
        assertThat(output).isNotNull();
        assertThat(output.id()).isEqualTo(createdId);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando produto não existe")
    void shouldThrowNotFoundExceptionWhenProductDoesNotExist() {
        // Given
        final var updateInput = new UpdateProductUseCase.Input(
                "invalid-id",
                "New Name", "New Desc", "New Cat", "New Brand", BigDecimal.valueOf(20.0), false
        );

        // When
        // Then
        assertThatThrownBy(() -> updateProductUseCase.execute(updateInput))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Product with id invalid-id was not found");
    }

    @Test
    @DisplayName("Deve lançar NotificationException quando dados são inválidos")
    void shouldThrowNotificationExceptionWhenDataIsInvalid() {
        // Given
        final var createInput = new CreateProductUseCase.Input(
                "Valid Name", "Valid Desc", "Cat", "Brand", BigDecimal.TEN, true
        );
        final var createdId = createProductUseCase.execute(createInput).id();

        final var updateInput = new UpdateProductUseCase.Input(
                createdId,
                "", "", "", "", BigDecimal.ZERO, true
        );

        // When
        // Then
        assertThatThrownBy(() -> updateProductUseCase.execute(updateInput))
                .isInstanceOf(NotificationException.class)
                .hasMessageContaining("Não foi possível atualizar o Agregado de Produto")
                .extracting(ex -> ((NotificationException) ex).getErrors().size())
                .isEqualTo(5); // name, description, category, brand, price
    }
}
