package com.example.catalog.domain.exception;

import com.example.catalog.domain.validation.Error;

import java.util.List;

/**
 * Exceção base que representa uma violação de regra de domínio.
 * <p>
 * Pode carregar um ou mais erros de validação. É uma `RuntimeException` pois
 * indica um estado inválido que não deveria ter ocorrido ou uma regra de negócio que foi quebrada.
 * </p>
 * <p>
 * Esta exceção é otimizada para performance: a stack trace é desabilitada (`writableStackTrace = false`)
 * no construtor, pois geralmente estamos interessados apenas na mensagem de erro e não na pilha de execução
 * para validações de domínio.
 * </p>
 */
public class DomainException extends RuntimeException {

    private final List<Error> errors;

    DomainException(final String message, final List<Error> errors) {
        super(message, null, true, false);
        this.errors = errors;
    }

    public static DomainException with(final Error error) {
        return new DomainException(error.message(), List.of(error));
    }

    public static DomainException with(final List<Error> errors) {
        final var message = errors.isEmpty() ? "" : errors.getFirst().message();
        return new DomainException(message, errors);
    }

    public List<Error> getErrors() {
        return errors;
    }
}
