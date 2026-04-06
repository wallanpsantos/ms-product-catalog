package com.example.catalog.application.usecase;

import com.example.catalog.UnitTest;
import com.example.catalog.application.port.output.ProductQueryGateway;
import com.example.catalog.application.port.output.dto.ProductSummary;
import com.example.catalog.domain.pagination.Pagination;
import com.example.catalog.domain.pagination.SearchQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
class DefaultListActiveProductsUseCaseTest {

    @Mock
    private ProductQueryGateway productGateway;

    @InjectMocks
    private DefaultListActiveProductsUseCase useCase;

    @Test
    @DisplayName("Deve listar produtos ativos com paginacao")
    void givenAValidQuery_whenCallListActiveProducts_thenShouldReturnPagination() {
        // Given
        final var query = new SearchQuery(0, 10, "", "name", "asc");
        final var summary = new ProductSummary(
                "id-1", "Notebook", "Desc", "Cat", "Brand", BigDecimal.TEN, true, LocalDateTime.now(), LocalDateTime.now()
        );
        final var expectedPagination = new Pagination<>(0, 10, 1, List.of(summary));

        when(productGateway.findAllActiveSummary(any(SearchQuery.class))).thenReturn(expectedPagination);

        // When
        final var result = useCase.execute(query);

        // Then
        assertThat(result)
                .isNotNull()
                .returns(expectedPagination.currentPage(), Pagination::currentPage)
                .returns(expectedPagination.perPage(), Pagination::perPage)
                .returns(expectedPagination.total(), Pagination::total);
        assertThat(result.items()).hasSize(1);

        final var output = result.items().getFirst();
        assertThat(output.id()).isEqualTo(summary.id());
        assertThat(output.name()).isEqualTo(summary.name());

        verify(productGateway).findAllActiveSummary(query);
    }
}