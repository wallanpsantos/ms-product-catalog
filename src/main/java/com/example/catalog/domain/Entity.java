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
 * <p>
 * <strong>Por que NÃO usamos {@code record} para Entidades?</strong>
 * <ol>
 *   <li><strong>Mutabilidade e Ciclo de Vida:</strong> Entidades mudam de estado ao longo do tempo. Records são projetados para serem imutáveis (JEP 395). Forçar a imutabilidade absoluta em entidades complexas exigiria recriar o objeto a cada mudança, prejudicando o encapsulamento e a modelagem do domínio.</li>
 *   <li><strong>Identidade vs. Igualdade Estrutural:</strong> A regra de ouro de uma Entidade é que ela é identificada <em>exclusivamente pelo seu ID</em>. Records geram {@code equals()} que compara <em>todos</em> os campos. Classes regulares permitem sobrescrever {@code equals()} e {@code hashCode()} para comparar apenas o ID (como feito nesta classe).</li>
 * </ol>
 * </p>
 *
 * @param <T> Tipo do identificador da entidade (deve estender {@link Identifier}).
 */
public abstract class Entity<T extends Identifier> extends AssertionConcern {

    protected final T id;

    protected Entity(final T id) {
        assertArgumentNotNull(id, "'id' must not be null");
        this.id = id;
    }

    public T getId() {
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
