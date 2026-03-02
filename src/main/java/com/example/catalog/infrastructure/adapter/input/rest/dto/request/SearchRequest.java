package com.example.catalog.infrastructure.adapter.input.rest.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para filtros de pesquisa de produtos.
 * <p>
 * Utilizado no endpoint de busca ({@code /search}) para encapsular os critérios
 * enviados pelo cliente. Garante que a query não seja vazia via validação.
 * </p>
 */
public record SearchRequest(
        @NotBlank(message = "Search query must not be blank")
        String query
) {
}
