package com.example.catalog.domain;

import java.util.Objects;

/**
 * Classe base para todos os identificadores de domínio.
 * <p>
 * Um Identificador é um tipo especial de Value Object usado para conferir identidade
 * única a uma Entidade.
 * </p>
 * <p>
 * Estende {@link AssertionConcern} para garantir que IDs nunca sejam criados com valores
 * nulos ou vazios, protegendo a integridade referencial do modelo.
 * </p>
 *
 * <h2>Por que {@code equals} e {@code hashCode} devem ser implementados aqui?</h2>
 * <p>
 * O Identificador é um <strong>Value Object</strong>: sua igualdade é definida pelo
 * <em>valor que carrega</em>, e não pela referência de memória. Sem sobrescrever esses
 * métodos, a JVM herda a implementação padrão de {@link Object}, que compara referências,
 * quebrando silenciosamente a semântica de igualdade por valor.
 * </p>
 * <p>
 * Implementar {@code equals} e {@code hashCode} <strong>nesta classe base</strong> é uma
 * decisão de design deliberada que garante o contrato de forma centralizada:
 * </p>
 * <ul>
 *   <li><strong>Contrato garantido por padrão:</strong> Qualquer novo identificador criado
 *   no futuro (ex: {@code CategoryID}, {@code BrandID}) herda automaticamente a igualdade
 *   por valor sem precisar reimplementar a lógica. Esquecer de sobrescrever esses métodos
 *   em subclasses causa bugs sutis e difíceis de rastrear, especialmente ao usar
 *   identificadores como chaves em {@link java.util.HashMap} ou elementos em
 *   {@link java.util.HashSet}.</li>
 *   <li><strong>Consistência com {@link Entity}:</strong> A classe {@code Entity} compara
 *   entidades pelo seu ID ({@code Objects.equals(id, entity.id)}). Para que essa comparação
 *   funcione corretamente, o próprio {@code Identifier} precisa ter {@code equals} baseado
 *   em valor — caso contrário, duas entidades com o mesmo ID em string seriam consideradas
 *   diferentes.</li>
 *   <li><strong>Performance em comparações reflexivas:</strong> A verificação
 *   {@code if (this == o) return true} evita o custo desnecessário de comparar os valores
 *   internos quando o objeto é comparado consigo mesmo, um ganho especialmente relevante
 *   em operações de coleções.</li>
 * </ul>
 */
public abstract class Identifier extends AssertionConcern implements ValueObject {

    public abstract String getValue();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Performance para comparações reflexivas, especialmente em coleções.
        if (o == null || getClass() != o.getClass()) return false;
        Identifier that = (Identifier) o;
        return Objects.equals(getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getValue());
    }

    @Override
    public String toString() {
        return getValue();
    }
}
