package com.example.catalog.infrastructure.adapter.output.gateway;

import com.example.catalog.UnitTest;
import com.example.catalog.domain.pagination.SearchQuery;
import com.example.catalog.domain.product.ProductID;
import com.example.catalog.infrastructure.adapter.output.persistence.ProductJpaEntity;
import com.example.catalog.infrastructure.adapter.output.persistence.ProductJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
class ProductJpaQueryAdapterTest {

    @Mock
    private ProductJpaRepository repository;

    @InjectMocks
    private ProductJpaQueryAdapter adapter;

    @Test
    @DisplayName("Deve buscar sumario por ID")
    void givenId_whenFindSummaryById_thenReturnSummary() {
        // Given
        final var id = ProductID.unique();
        final var entity = new ProductJpaEntity(id.getValue(), "N", "D", "C", "B", BigDecimal.TEN, null, LocalDateTime.now(), LocalDateTime.now());
        when(repository.findById(id.getValue())).thenReturn(Optional.of(entity));

        // When
        final var result = adapter.findSummaryById(id);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(id.getValue());
        assertThat(result.get().active()).isFalse(); // due to null fallback
        verify(repository).findById(id.getValue());
    }

    @Test
    @DisplayName("Deve listar sumarios de produtos ativos com paginacao (asc)")
    void givenSearchQuery_whenFindAllActiveSummary_thenReturnPagination() {
        // Given
        final var query = new SearchQuery(0, 10, "", "name", "asc");
        final var entity = new ProductJpaEntity("id", "N", "D", "C", "B", BigDecimal.TEN, true, LocalDateTime.now(), LocalDateTime.now());
        final var page = new PageImpl<>(List.of(entity));

        when(repository.findAllByActiveTrue(any(PageRequest.class))).thenReturn(page);

        // When
        final var result = adapter.findAllActiveSummary(query);

        // Then
        assertThat(result.items()).hasSize(1);
        assertThat(result.total()).isEqualTo(1);
        verify(repository).findAllByActiveTrue(any(PageRequest.class));
    }

    @Test
    @DisplayName("Deve listar sumarios de produtos ativos com paginacao (desc)")
    void givenSearchQueryDesc_whenFindAllActiveSummary_thenReturnPagination() {
        // Given
        final var query = new SearchQuery(0, 10, "", "name", "desc");
        final var entity = new ProductJpaEntity("id", "N", "D", "C", "B", BigDecimal.TEN, true, LocalDateTime.now(), LocalDateTime.now());
        final var page = new PageImpl<>(List.of(entity));

        when(repository.findAllByActiveTrue(any(PageRequest.class))).thenReturn(page);

        // When
        final var result = adapter.findAllActiveSummary(query);

        // Then
        assertThat(result.items()).hasSize(1);
        assertThat(result.total()).isEqualTo(1);
        verify(repository).findAllByActiveTrue(any(PageRequest.class));
    }

    @Test
    @DisplayName("Deve buscar produtos pelo termo")
    void givenTerm_whenSearchProductsSummary_thenReturnList() {
        // Given
        final var term = "term";
        final var entity = new ProductJpaEntity("id", "N", "D", "C", "B", BigDecimal.TEN, true, LocalDateTime.now(), LocalDateTime.now());
        when(repository.searchActiveProducts(anyString())).thenReturn(List.of(entity));

        // When
        final var result = adapter.searchProductsSummary(term);

        // Then
        assertThat(result).hasSize(1);
        verify(repository).searchActiveProducts(term);
    }
}