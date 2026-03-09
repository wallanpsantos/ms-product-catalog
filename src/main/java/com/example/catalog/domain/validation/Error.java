package com.example.catalog.domain.validation;

import java.io.Serializable;

/**
 * Representa um único erro de validação no domínio.
 * Utilizado por {@link ValidationHandler} para acumular erros (Notification Pattern).
 */
public record Error(String message) implements Serializable {
}
