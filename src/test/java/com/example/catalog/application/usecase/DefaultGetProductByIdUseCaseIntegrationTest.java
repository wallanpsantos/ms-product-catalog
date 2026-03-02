package com.example.catalog.application.usecase;

import com.example.catalog.IntegrationTest;
import com.example.catalog.application.port.input.CreateProductUseCase;
import com.example.catalog.application.port.input.GetProductByIdUseCase;
import com.example.catalog.domain.exception.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@IntegrationTest
class DefaultGetProductByIdUseCaseIntegrationTest {

    @Autowired
    private GetProductByIdUseCase getProductByIdUseCase;

    @Autowired
    private CreateProductUseCase createProductUseCase;

    @Test
    @DisplayName("Deve buscar produto por ID com sucesso")
    void shouldGetProductByIdSuccessfully() {
        // Given
        final var createInput = new CreateProductUseCase.Input(
                "Notebook Pro", "Notebook de alta performance", "Eletrônicos", "TechBrand", BigDecimal.valueOf(4999.90), true
        );
        final var createdId = createProductUseCase.execute(createInput).id();

        final var input = new GetProductByIdUseCase.Input(createdId);

        // When
        final var output = getProductByIdUseCase.execute(input);

        // Then
        assertThat(output).isNotNull();
        assertThat(output.id()).isEqualTo(createdId);
        assertThat(output.name()).isEqualTo("Notebook Pro");
        assertThat(output.description()).isEqualTo("Notebook de alta performance");
        assertThat(output.category()).isEqualTo("Eletrônicos");
        assertThat(output.brand()).isEqualTo("TechBrand");
        assertThat(output.price()).isEqualByComparingTo(BigDecimal.valueOf(4999.90));
        assertThat(output.active()).isTrue();
        assertThat(output.createdAt()).isNotNull();
        assertThat(output.updatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando ID não existe")
    void shouldThrowNotFoundExceptionWhenIdDoesNotExist() {
        // Given
        final var input = new GetProductByIdUseCase.Input("invalid-id");

        // When
        // Then
        assertThatThrownBy(() -> getProductByIdUseCase.execute(input))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Product with id invalid-id was not found");
    }
}
