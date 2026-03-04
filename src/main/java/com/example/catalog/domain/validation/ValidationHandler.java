package com.example.catalog.domain.validation;

import java.util.List;
import java.util.Optional;

/**
 * Contrato para o padrão **Notification Pattern**.
 * <p>
 * Diferente do fail-fast (que lança exceção no primeiro erro), um ValidationHandler
 * é responsável por acumular erros de validação, permitindo que o usuário receba
 * um relatório completo do que está errado em sua requisição.
 * </p>
 * <p>
 * <strong>Principais Responsabilidades:</strong>
 * <ul>
 *   <li>Acumular erros (append).</li>
 *   <li>Verificar se há erros (hasError).</li>
 *   <li>Executar validações seguras que capturam exceções e convertem em erros (validate).</li>
 * </ul>
 * </p>
 */
public interface ValidationHandler {

    ValidationHandler append(Error anError);

    ValidationHandler append(ValidationHandler anHandler);

    <T> Optional<T> validate(Validation<T> aValidation);

    List<Error> getErrors();

    default boolean hasError() {
        return getErrors() != null && !getErrors().isEmpty();
    }

    default Optional<Error> firstError() {
        if (getErrors() != null && !getErrors().isEmpty()) {
            return Optional.of(getErrors().getFirst());
        } else {
            return Optional.empty();
        }
    }

    interface Validation<T> {
        T validate();
    }
}
