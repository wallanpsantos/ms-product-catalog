package com.example.catalog.domain.exception;

import com.example.catalog.domain.validation.Error;

/**
 * Sinaliza que um recurso solicitado não foi encontrado.
 * <p>
 * Geralmente mapeada para o status HTTP 404 (Not Found) na camada de apresentação.
 * Estende {@link DomainException} para manter a hierarquia de exceções de domínio unificada.
 * </p>
 */
public class NotFoundException extends DomainException {

    private NotFoundException(final String message) {
        super(message, java.util.List.of(new Error(message)));
    }

    public static NotFoundException with(final Class<?> clazz, final String id) {
        final var message = "%s with id %s was not found".formatted(clazz.getSimpleName(), id);
        return new NotFoundException(message);
    }
}
