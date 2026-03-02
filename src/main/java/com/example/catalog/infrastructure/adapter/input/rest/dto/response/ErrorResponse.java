package com.example.catalog.infrastructure.adapter.input.rest.dto.response;

import com.example.catalog.domain.validation.Error;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Estrutura padronizada para retorno de erros da API.
 * <p>
 * Segue boas práticas de design de API, fornecendo status HTTP, mensagem descritiva,
 * timestamp e, opcionalmente, uma lista detalhada de erros de validação.
 * Utilizado pelo {@link com.example.catalog.infrastructure.adapter.input.exception.GlobalExceptionHandler}.
 * </p>
 */
public record ErrorResponse(
        int status,
        String message,
        List<Error> errors,
        LocalDateTime timestamp
) {
    public ErrorResponse(int status, String message, LocalDateTime timestamp) {
        this(status, message, null, timestamp);
    }
}
