package com.example.catalog.application.port.input;

import com.example.catalog.application.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Porta de Entrada para listar produtos ativos com paginação.
 * <p>
 * Otimizado para consultas de catálogo, retornando apenas produtos onde {@code active = true}.
 * Suporta paginação e ordenação via objeto {@link org.springframework.data.domain.Pageable}.
 * </p>
 */
public abstract class ListActiveProductsUseCase
        extends Query<Pageable, Page<ListActiveProductsUseCase.Output>> {

    public record Output(
            String id,
            String name,
            String description,
            String category,
            String brand,
            BigDecimal price,
            boolean active,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
    }
}
