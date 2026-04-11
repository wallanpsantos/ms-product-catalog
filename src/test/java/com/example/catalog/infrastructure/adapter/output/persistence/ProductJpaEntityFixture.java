package com.example.catalog.infrastructure.adapter.output.persistence;

import java.math.BigDecimal;
import java.time.Instant;

public class ProductJpaEntityFixture {

    private ProductJpaEntityFixture() {
    }

    public static ProductJpaEntity active(final String id, final String name) {
        return new ProductJpaEntity(id, name, "Desc", "Cat", "Brand",
                new BigDecimal("100.00"), true, Instant.now(), Instant.now());
    }

    public static ProductJpaEntity inactive(final String id) {
        return inactive(id, "Inativo");
    }

    public static ProductJpaEntity inactive(final String id, final String name) {
        return new ProductJpaEntity(id, name, "Desc", "Cat", "Brand",
                new BigDecimal("100.00"), false, Instant.now(), Instant.now());
    }
}
