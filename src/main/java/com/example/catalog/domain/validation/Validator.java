package com.example.catalog.domain.validation;

/**
 * Classe base para validadores de domínio.
 * <p>
 * Um Validator encapsula as regras de validação complexas de uma Entidade ou Agregado.
 * Ele recebe um {@link ValidationHandler} para onde deve reportar os erros encontrados,
 * em vez de lançar exceções diretamente.
 * </p>
 * <p>
 * Isso separa a responsabilidade: a Entidade contém os dados, o Validator contém as regras
 * de integridade, e o Handler contém o estado da validação (erros).
 * </p>
 */
public abstract class Validator {

    private final ValidationHandler handler;

    protected Validator(final ValidationHandler handler) {
        this.handler = handler;
    }

    public abstract void validate();

    protected ValidationHandler validationHandler() {
        return this.handler;
    }
}
