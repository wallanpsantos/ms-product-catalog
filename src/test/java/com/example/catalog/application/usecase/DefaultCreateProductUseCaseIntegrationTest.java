package com.example.catalog.application.usecase;

import com.example.catalog.IntegrationTest;
import com.example.catalog.application.port.input.CreateProductUseCase;
import com.example.catalog.domain.exception.NotificationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@IntegrationTest
class DefaultCreateProductUseCaseIntegrationTest {

    @Autowired
    private CreateProductUseCase createProductUseCase;

    @Test
    @DisplayName("Deve criar produto com sucesso e retornar ID persistido")
    void shouldCreateProductAndReturnPersistedId() {
        // Given
        final var input = new CreateProductUseCase.Input(
                "Notebook Pro",
                "Notebook de alta performance",
                "Eletrônicos",
                "TechBrand",
                BigDecimal.valueOf(4999.90),
                true
        );

        // When
        final var output = createProductUseCase.execute(input);

        // Then
        assertThat(output).isNotNull();
        assertThat(output.id()).isNotBlank();
    }

    @Test
    @DisplayName("Deve defaultar active para true quando não informado")
    void shouldDefaultActiveToTrueWhenNull() {
        // Given
        final var input = new CreateProductUseCase.Input(
                "Mouse Gamer",
                "Mouse com DPI ajustável",
                "Periféricos",
                "GamingBrand",
                BigDecimal.valueOf(299.90),
                null
        );

        // When
        final var output = createProductUseCase.execute(input);

        // Then
        assertThat(output.id()).isNotBlank();
    }

    @Test
    @DisplayName("Deve gerar IDs únicos para produtos distintos")
    void shouldGenerateDistinctIdsForDifferentProducts() {
        // Given
        final var input1 = new CreateProductUseCase.Input(
                "Produto A", "Desc A", "Cat", "Brand", BigDecimal.TEN, true
        );
        final var input2 = new CreateProductUseCase.Input(
                "Produto B", "Desc B", "Cat", "Brand", BigDecimal.ONE, true
        );

        // When
        final var output1 = createProductUseCase.execute(input1);
        final var output2 = createProductUseCase.execute(input2);

        // Then
        assertThat(output1.id()).isNotEqualTo(output2.id());
    }

    @Test
    @DisplayName("Deve lançar NotificationException quando nome está em branco")
    void shouldThrowNotificationExceptionWhenNameIsBlank() {
        // Given
        final var input = new CreateProductUseCase.Input(
                "",
                "Descrição",
                "Categoria",
                "Marca",
                BigDecimal.valueOf(100.0),
                true
        );

        // When
        // Then
        assertThatThrownBy(() -> createProductUseCase.execute(input))
                .isInstanceOf(NotificationException.class)
                .hasMessageContaining("Não foi possível criar o Agregado de Produto");
    }

    @Test
    @DisplayName("Deve lançar NotificationException quando preço é nulo")
    void shouldThrowNotificationExceptionWhenPriceIsNull() {
        // Given
        final var input = new CreateProductUseCase.Input(
                "Produto Válido",
                "Descrição",
                "Categoria",
                "Marca",
                null,
                true
        );

        // When
        // Then
        assertThatThrownBy(() -> createProductUseCase.execute(input))
                .isInstanceOf(NotificationException.class)
                .hasMessageContaining("Não foi possível criar o Agregado de Produto");
    }
}