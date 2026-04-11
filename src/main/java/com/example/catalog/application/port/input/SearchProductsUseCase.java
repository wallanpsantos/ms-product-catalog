package com.example.catalog.application.port.input;

import com.example.catalog.application.Query;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Porta de Entrada para pesquisa textual de produtos.
 * <p>
 * Permite buscar produtos por termos que coincidam com nome, descrição, marca ou categoria.
 * A busca é case-insensitive e filtra apenas produtos ativos.
 * </p>
 */
public abstract class SearchProductsUseCase
        extends Query<SearchProductsUseCase.Input, List<SearchProductsUseCase.Output>> {

    public record Input(String query) {
    }

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
