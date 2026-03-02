package com.example.catalog.application.port.input;

import com.example.catalog.application.Command;

import java.util.List;

/**
 * Porta de Entrada para criar múltiplos produtos em lote.
 * Utiliza Listas de Inputs e Outputs do UseCase unitário.
 */
public abstract class CreateProductBatchUseCase
        extends Command<List<CreateProductUseCase.Input>, List<CreateProductUseCase.Output>> {
}
