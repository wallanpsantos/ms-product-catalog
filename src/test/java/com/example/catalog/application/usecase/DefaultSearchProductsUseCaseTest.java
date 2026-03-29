package com.example.catalog.application.usecase;

import com.example.catalog.UnitTest;
import com.example.catalog.application.port.input.SearchProductsUseCase;
import com.example.catalog.application.port.output.ProductQueryGateway;
import com.example.catalog.application.port.output.dto.ProductSummary;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
class DefaultSearchProductsUseCaseTest {

    @Mock
    private ProductQueryGateway productGateway;

    @InjectMocks
    private DefaultSearchProductsUseCase useCase;

    @Test
    @DisplayName("Deve buscar produtos com sucesso")
    void givenAValidQuery_whenCallSearch_thenShouldReturnProducts() {
        // Given
        final var query = "Notebook";
        final var input = new SearchProductsUseCase.Input(query);
        final var summary = new ProductSummary(
                "id-1", "Notebook", "Desc", "Cat", "Brand", BigDecimal.TEN, true, LocalDateTime.now(), LocalDateTime.now()
        );

        when(productGateway.searchProductsSummary(anyString())).thenReturn(List.of(summary));

        // When
        final var results = useCase.execute(input);

        // Then
        assertThat(results).hasSize(1);
        final var output = results.getFirst();
        assertThat(output.id()).isEqualTo(summary.id());
        assertThat(output.name()).isEqualTo(summary.name());

        verify(productGateway).searchProductsSummary(query);
    }
}