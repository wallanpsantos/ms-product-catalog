package com.example.catalog.application.usecase;

import com.example.catalog.UnitTest;
import com.example.catalog.application.port.input.GetProductByIdUseCase;
import com.example.catalog.application.port.output.ProductQueryGateway;
import com.example.catalog.application.port.output.dto.ProductSummary;
import com.example.catalog.domain.exception.NotFoundException;
import com.example.catalog.domain.product.ProductID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
class DefaultGetProductByIdUseCaseTest {

    @InjectMocks
    private DefaultGetProductByIdUseCase useCase;

    @Mock
    private ProductQueryGateway productGateway;

    @Test
    @DisplayName("Deve buscar produto por ID com sucesso")
    void givenValidId_whenExecute_thenReturnOutput() {
        // Given
        final var expectedId = UUID.randomUUID().toString();
        final var expectedName = "Notebook Pro";
        final var expectedDescription = "Notebook de alta performance";
        final var expectedCategory = "Electronics";
        final var expectedBrand = "BrandX";
        final var expectedPrice = new BigDecimal("4999.99");
        final var expectedActive = true;
        final var expectedCreatedAt = Instant.now();
        final var expectedUpdatedAt = Instant.now();

        final var input = new GetProductByIdUseCase.Input(expectedId);

        final var productSummary = new ProductSummary(
                expectedId, expectedName, expectedDescription, expectedCategory,
                expectedBrand, expectedPrice, expectedActive, expectedCreatedAt, expectedUpdatedAt
        );

        when(productGateway.findSummaryById(any(ProductID.class)))
                .thenReturn(Optional.of(productSummary));

        // When
        final var output = useCase.execute(input);

        // Then
        assertThat(output)
                .isNotNull()
                .returns(expectedId, GetProductByIdUseCase.Output::id)
                .returns(expectedName, GetProductByIdUseCase.Output::name)
                .returns(expectedDescription, GetProductByIdUseCase.Output::description)
                .returns(expectedCategory, GetProductByIdUseCase.Output::category)
                .returns(expectedBrand, GetProductByIdUseCase.Output::brand)
                .returns(expectedPrice, GetProductByIdUseCase.Output::price)
                .returns(expectedActive, GetProductByIdUseCase.Output::active)
                .returns(expectedCreatedAt, GetProductByIdUseCase.Output::createdAt)
                .returns(expectedUpdatedAt, GetProductByIdUseCase.Output::updatedAt);

        verify(productGateway, times(1)).findSummaryById(ProductID.from(expectedId));
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando o produto não existir")
    void givenInvalidId_whenExecute_thenThrowNotFoundException() {
        // Given
        final var expectedId = UUID.randomUUID().toString();
        final var input = new GetProductByIdUseCase.Input(expectedId);

        when(productGateway.findSummaryById(any(ProductID.class)))
                .thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Product with id %s was not found", expectedId);

        verify(productGateway, times(1)).findSummaryById(ProductID.from(expectedId));
    }
}
