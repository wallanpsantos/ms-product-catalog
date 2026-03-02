package com.example.catalog.application;

/**
 * Classe base abstrata para todos os Casos de Uso (Application Services).
 * <p>
 * Implementa o padrão <strong>Command</strong>. Cada subclasse representa uma única
 * operação de negócio do sistema (ex: Criar Produto, Atualizar Produto).
 * </p>
 * <p>
 * Segue o princípio da Responsabilidade Única (SRP) ao nível de arquitetura:
 * uma classe para cada ação do usuário.
 * </p>
 *
 * @param <IN>  Tipo do objeto de entrada (Input DTO/Record).
 * @param <OUT> Tipo do objeto de saída (Output DTO/Record).
 */
public abstract class UseCase<IN, OUT> {

    public abstract OUT execute(IN anIn);

    public <T> T execute(IN anIn, Presenter<OUT, T> aPresenter) {
        if (aPresenter == null) {
            throw new IllegalArgumentException("UseCase 'presenter' is required");
        }

        return aPresenter.apply(execute(anIn));
    }
}
