package com.example.catalog.application.port.input;

import com.example.catalog.application.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Porta de Entrada para buscar um produto pelo seu Identificador único.
 * <p>
 * Se o produto não for encontrado, a implementação deve lançar uma exceção de
 * domínio apropriada (ex: {@code NotFoundException}).
 * </p>
 */
public abstract class GetProductByIdUseCase
        extends Query<GetProductByIdUseCase.Input, GetProductByIdUseCase.Output> {

    public record Input(String id) {
    }

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
