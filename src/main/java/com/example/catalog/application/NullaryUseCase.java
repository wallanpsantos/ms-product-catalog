package com.example.catalog.application;

/**
 * Especialização de {@link UseCase} para operações que não requerem parâmetros de entrada.
 * <p>
 * Útil para casos de uso como "Listar todos os produtos disponíveis" ou "Gerar relatório diário",
 * onde o contexto necessário já está disponível via injeção de dependência ou não é necessário.
 * </p>
 *
 * @param <OUT> Tipo do objeto de saída.
 */
public abstract class NullaryUseCase<OUT> {

    public abstract OUT execute();

    public <T> T execute(Presenter<OUT, T> aPresenter) {
        if (aPresenter == null) {
            throw new IllegalArgumentException("NullaryUseCase 'presenter' is required");
        }

        return aPresenter.apply(execute());
    }
}
