package com.example.catalog.domain.validation.handler;

import com.example.catalog.domain.exception.DomainException;
import com.example.catalog.domain.validation.Error;
import com.example.catalog.domain.validation.ValidationHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementação concreta do {@link ValidationHandler} que armazena erros em uma lista em memória.
 * <p>
 * Esta classe é o coração da validação de domínio "amigável". Ela permite que uma entidade
 * ou caso de uso continue executando mesmo após encontrar um problema, coletando todos
 * os defeitos para reportá-los de uma só vez no final do processo.
 * </p>
 * <p>
 * <strong>Exemplo de Uso:</strong>
 * <pre>
 * Notification notification = Notification.create();
 * product.validate(notification);
 *
 * if (notification.hasError()) {
 *     // Lança exceção com TODOS os erros encontrados
 *     throw NotificationException.with("Erro ao criar produto", notification);
 * }
 * </pre>
 * </p>
 */
public class Notification implements ValidationHandler {

    private final List<Error> errors;

    private Notification(final List<Error> errors) {
        this.errors = errors;
    }

    public static Notification create() {
        return new Notification(new ArrayList<>());
    }

    public static Notification create(final Throwable t) {
        return create(new Error(t.getMessage()));
    }

    public static Notification create(final Error anError) {
        return new Notification(new ArrayList<>()).append(anError);
    }

    public static Notification create(final List<Error> errors) {
        return new Notification(new ArrayList<>(errors));
    }

    @Override
    public Notification append(final Error anError) {
        this.errors.add(anError);
        return this;
    }

    @Override
    public Notification append(final ValidationHandler anHandler) {
        this.errors.addAll(anHandler.getErrors());
        return this;
    }

    @Override
    public <T> T validate(final Validation<T> aValidation) {
        try {
            return aValidation.validate();
        } catch (final DomainException ex) {
            this.errors.addAll(ex.getErrors());
        } catch (final Throwable t) {
            this.errors.add(new Error(t.getMessage()));
        }
        return null;
    }

    @Override
    public List<Error> getErrors() {
        return this.errors;
    }
}
