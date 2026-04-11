package com.example.catalog.domain;

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
 */
public abstract class Identifier extends AssertionConcern implements ValueObject {

    public abstract String getValue();
    
    @Override
    public String toString() {
        return getValue();
    }

}
