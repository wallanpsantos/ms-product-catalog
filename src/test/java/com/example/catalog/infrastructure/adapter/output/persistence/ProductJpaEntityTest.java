package com.example.catalog.infrastructure.adapter.output.persistence;

import com.example.catalog.UnitTest;
import com.example.catalog.domain.product.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
class ProductJpaEntityTest {

    @Test
    @DisplayName("Deve mapear de Product para ProductJpaEntity e vice-versa")
    void givenProduct_whenMap_thenShouldMapCorrectly() {
        // Given
        final var product = Product.newProduct("Name", "Desc", "Cat", "Brand", BigDecimal.TEN, true);

        // When
        final var entity = ProductJpaEntity.from(product);
        final var mappedProduct = entity.toEntity();

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(product.getId().getValue());
        assertThat(entity.getName()).isEqualTo(product.getName());
        assertThat(entity.getDescription()).isEqualTo(product.getDescription());
        assertThat(entity.getCategory()).isEqualTo(product.getCategory());
        assertThat(entity.getBrand()).isEqualTo(product.getBrand());
        assertThat(entity.getPrice()).isEqualTo(product.getPrice());
        assertThat(entity.getActive()).isEqualTo(product.isActive());
        assertThat(entity.getCreatedAt()).isEqualTo(product.getCreatedAt());
        assertThat(entity.getUpdatedAt()).isEqualTo(product.getUpdatedAt());

        assertThat(mappedProduct).isNotNull();
        assertThat(mappedProduct.getId()).isEqualTo(product.getId());
        assertThat(mappedProduct.getName()).isEqualTo(product.getName());
    }

    @Test
    @DisplayName("Deve testar getters e setters")
    void givenEntity_whenSetProperties_thenShouldGetCorrectly() {
        final var entity = new ProductJpaEntity();
        final var now = LocalDateTime.now();

        entity.setId("id");
        entity.setName("n");
        entity.setDescription("d");
        entity.setCategory("c");
        entity.setBrand("b");
        entity.setPrice(BigDecimal.ONE);
        entity.setActive(false);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        assertThat(entity.getId()).isEqualTo("id");
        assertThat(entity.getName()).isEqualTo("n");
        assertThat(entity.getDescription()).isEqualTo("d");
        assertThat(entity.getCategory()).isEqualTo("c");
        assertThat(entity.getBrand()).isEqualTo("b");
        assertThat(entity.getPrice()).isEqualTo(BigDecimal.ONE);
        assertThat(entity.getActive()).isFalse();
        assertThat(entity.getCreatedAt()).isEqualTo(now);
        assertThat(entity.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Deve testar equals e hashCode")
    void givenEntities_whenCompare_thenShouldReturnCorrectly() {
        final var entity1 = new ProductJpaEntity();
        entity1.setId("1");

        final var entity2 = new ProductJpaEntity();
        entity2.setId("1");

        final var entity3 = new ProductJpaEntity();
        entity3.setId("2");

        assertThat(entity1)
                .isEqualTo(entity2)
                .isNotEqualTo(entity3)
                .isNotNull()
                .isNotEqualTo(new Object())
                .hasSameHashCodeAs(entity2);

        assertThat(entity1.hashCode()).isNotEqualTo(entity3.hashCode());    }

    @Test
    @DisplayName("Deve testar toString")
    void givenEntity_whenToString_thenShouldReturnString() {
        final var entity = new ProductJpaEntity();
        entity.setId("1");
        assertThat(entity.toString()).contains("id='1'");
    }
}