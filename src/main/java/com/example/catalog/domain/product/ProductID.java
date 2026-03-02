package com.example.catalog.domain.product;

import com.example.catalog.domain.Identifier;

import java.util.UUID;

/**
 * Identificador fortemente tipado para a entidade {@link Product}.
 * <p>
 * Evita o uso de Strings genéricas para IDs, prevenindo erros como passar um ID de
 * Usuário onde se espera um ID de Produto (Primitive Obsession).
 * </p>
 */
public class ProductID extends Identifier {

    private final String value;

    private ProductID(final String value) {
        this.assertArgumentNotEmpty(value, "id must not be empty or null");
        this.value = value;
    }

    public static ProductID unique() {
        return new ProductID(UUID.randomUUID().toString());
    }

    public static ProductID from(final String value) {
        return new ProductID(value);
    }

    @Override
    public String getValue() {
        return value;
    }
}
