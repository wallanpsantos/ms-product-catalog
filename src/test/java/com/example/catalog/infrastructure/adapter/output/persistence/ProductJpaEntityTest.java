package com.example.catalog.infrastructure.adapter.output.persistence;

import com.example.catalog.UnitTest;
import com.example.catalog.domain.product.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

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
        assertThat(entity)
                .isNotNull()
                .returns(product.getId().getValue(), ProductJpaEntity::getId)
                .returns(product.getName(), ProductJpaEntity::getName)
                .returns(product.getDescription(), ProductJpaEntity::getDescription)
                .returns(product.getCategory(), ProductJpaEntity::getCategory)
                .returns(product.getBrand(), ProductJpaEntity::getBrand)
                .returns(product.getPrice(), ProductJpaEntity::getPrice)
                .returns(product.isActive(), ProductJpaEntity::getActive)
                .returns(product.getCreatedAt(), ProductJpaEntity::getCreatedAt)
                .returns(product.getUpdatedAt(), ProductJpaEntity::getUpdatedAt);

        assertThat(mappedProduct)
                .isNotNull()
                .returns(product.getId(), Product::getId)
                .returns(product.getName(), Product::getName);
    }


    @Test
    @DisplayName("Deve testar equals e hashCode")
    void givenEntities_whenCompare_thenShouldReturnCorrectly() {
        // Given
        final var entity1 = new ProductJpaEntity();
        entity1.setId("1");

        final var entity2 = new ProductJpaEntity();
        entity2.setId("1");

        final var entity3 = new ProductJpaEntity();
        entity3.setId("2");

        // When / Then
        assertThat(entity1)
                .isEqualTo(entity2)
                .isNotEqualTo(entity3)
                .isNotNull()
                .isNotEqualTo(new Object())
                .hasSameHashCodeAs(entity2);

        assertThat(entity1.hashCode()).isNotEqualTo(entity3.hashCode());
    }

    @Test
    @DisplayName("Deve testar toString")
    void givenEntity_whenToString_thenShouldReturnString() {
        // Given
        final var entity = new ProductJpaEntity();
        entity.setId("1");

        // When
        final var result = entity.toString();

        // Then
        assertThat(result).contains("id='1'");
    }
}