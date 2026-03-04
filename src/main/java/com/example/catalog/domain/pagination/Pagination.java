package com.example.catalog.domain.pagination;

import java.util.List;
import java.util.function.Function;

/**
 * Representa uma página de resultados, independente de framework.
 * Mantém o core da aplicação livre de dependências como org.springframework.data.domain.Page.
 *
 * @param <T> Tipo do conteúdo da página
 */
public record Pagination<T>(
        int currentPage,
        int perPage,
        long total,
        List<T> items
) {
    public <R> Pagination<R> map(final Function<T, R> mapper) {
        final List<R> aNewList = this.items.stream().map(mapper).toList();
        return new Pagination<>(currentPage(), perPage(), total(), aNewList);
    }
}
