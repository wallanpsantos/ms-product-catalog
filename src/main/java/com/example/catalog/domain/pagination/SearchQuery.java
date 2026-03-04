package com.example.catalog.domain.pagination;

/**
 * Representa os critérios de uma busca paginada, independente de framework.
 * Mantém o core da aplicação livre de dependências como org.springframework.data.domain.Pageable.
 */
public record SearchQuery(
        int page,
        int perPage,
        String terms,
        String sort,
        String direction
) {
}
