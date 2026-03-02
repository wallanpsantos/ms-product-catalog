package com.example.catalog.application.port.input;

import com.example.catalog.application.Command;

import java.math.BigDecimal;

/**
 * Porta de Entrada para atualização completa de um produto existente.
 * <p>
 * Recebe todos os dados do produto. A implementação deve validar se o produto existe,
 * aplicar as alterações e persistir.
 * </p>
 */
public abstract class UpdateProductUseCase
        extends Command<UpdateProductUseCase.Input, UpdateProductUseCase.Output> {

    public record Input(
            String id,
            String name,
            String description,
            String category,
            String brand,
            BigDecimal price,
            boolean active
    ) {
    }

    public record Output(String id) {
    }
}
