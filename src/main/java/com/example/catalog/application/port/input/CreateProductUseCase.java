package com.example.catalog.application.port.input;

import com.example.catalog.application.Command;

import java.math.BigDecimal;

/**
 * Porta de Entrada para criar um novo produto.
 * Utiliza Records para Inputs e Outputs imutáveis.
 */
public abstract class CreateProductUseCase
        extends Command<CreateProductUseCase.Input, CreateProductUseCase.Output> {

    public record Input(
            String name,
            String description,
            String category,
            String brand,
            BigDecimal price,
            Boolean active
    ) {
    }

    public record Output(String id) {
    }
}
