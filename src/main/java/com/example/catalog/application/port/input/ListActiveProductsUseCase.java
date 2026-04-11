package com.example.catalog.application.port.input;

import com.example.catalog.application.Query;
import com.example.catalog.domain.pagination.Pagination;
import com.example.catalog.domain.pagination.SearchQuery;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Porta de Entrada para listar produtos ativos com paginação.
 * <p>
 * Otimizado para consultas de catálogo, retornando apenas produtos onde {@code active = true}.
 * Suporta paginação e ordenação via objeto {@link SearchQuery}.
 * </p>
 */
public abstract class ListActiveProductsUseCase
        extends Query<SearchQuery, Pagination<ListActiveProductsUseCase.Output>> {

    public record Output(
            String id,
            String name,
            String description,
            String category,
            String brand,
            BigDecimal price,
            boolean active,
            Instant createdAt,
            Instant updatedAt
    ) {
    }
}
