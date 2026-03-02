package com.example.catalog.application.port.input;

import com.example.catalog.application.Command;

/**
 * Porta de Entrada para desativar (Soft Delete) um produto.
 * <p>
 * Define o contrato para a operação de alterar o estado do produto para inativo,
 * preservando o histórico mas removendo-o das listagens ativas.
 * </p>
 */
public abstract class DeactivateProductUseCase extends Command<DeactivateProductUseCase.Input, DeactivateProductUseCase.Output> {

    public record Input(String id) {
    }

    public record Output() {
    }
}
