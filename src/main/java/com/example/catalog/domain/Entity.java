package com.example.catalog.domain;

import com.example.catalog.domain.validation.ValidationHandler;

import java.util.Objects;

/**
 * Classe base abstrata para todas as Entidades do domínio.
 * <p>
 * Uma <strong>Entidade</strong> é um objeto fundamental do domínio que é definido por sua
 * identidade única (ID) e continuidade, e não apenas pelos seus atributos (como ocorre com
 * Value Objects). Duas entidades com os mesmos atributos mas IDs diferentes são distintas.
 * </p>
 * <p>
 * Esta classe fornece:
 * <ul>
 *   <li>Identidade única e imutável ({@code id}).</li>
 *   <li>Implementação padrão de {@code equals} e {@code hashCode} baseada no ID.</li>
 *   <li>Mecanismos de validação defensiva (via herança de {@link AssertionConcern}).</li>
 * </ul>
 * </p>
 *
 * @param <ID> Tipo do identificador da entidade (deve estender {@link Identifier}).
 */
public abstract class Entity<ID extends Identifier> extends AssertionConcern {

    protected final ID id;

    protected Entity(final ID id) {
        assertArgumentNotNull(id, "'id' must not be null");
        this.id = id;
    }

    public ID getId() {
        return id;
    }

    /**
     * Validates the entity's invariants using the Notification Pattern.
     * Accumulates all errors instead of throwing on the first violation.
     *
     * @param handler the validation handler that collects errors
     */
    public abstract void validate(ValidationHandler handler);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity<?> entity = (Entity<?>) o;
        return Objects.equals(id, entity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
