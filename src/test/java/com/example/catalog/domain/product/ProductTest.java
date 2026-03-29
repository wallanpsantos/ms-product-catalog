package com.example.catalog.domain.product;

import com.example.catalog.UnitTest;
import com.example.catalog.domain.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@UnitTest
class ProductTest {

    @Test
    @DisplayName("Deve instanciar um novo produto valido")
    void givenValidParams_whenCallNewProduct_thenInstantiateAProduct() {
        // Given
        final var expectedName = "Notebook";
        final var expectedDescription = "Notebook de alta performance";
        final var expectedCategory = "Eletronicos";
        final var expectedBrand = "Dell";
        final var expectedPrice = new BigDecimal("5000.00");
        final var expectedActive = true;

        // When
        final var actualProduct = Product.newProduct(
                expectedName,
                expectedDescription,
                expectedCategory,
                expectedBrand,
                expectedPrice,
                expectedActive
        );

        // Then
        assertThat(actualProduct)
                .isNotNull()
                .returns(expectedName, Product::getName)
                .returns(expectedDescription, Product::getDescription)
                .returns(expectedCategory, Product::getCategory)
                .returns(expectedBrand, Product::getBrand)
                .returns(expectedPrice, Product::getPrice)
                .returns(true, Product::isActive);
        assertThat(actualProduct.getId()).isNotNull();
        assertThat(actualProduct.getCreatedAt()).isNotNull();
        assertThat(actualProduct.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Deve atualizar um produto com sucesso")
    void givenAValidProduct_whenCallUpdate_thenUpdateProduct() {
        // Given
        final var product = Product.newProduct("Name", "Desc", "Cat", "Brand", new BigDecimal("10.0"), true);
        final var expectedName = "Notebook";
        final var expectedDescription = "Notebook de alta performance";
        final var expectedCategory = "Eletronicos";
        final var expectedBrand = "Dell";
        final var expectedPrice = new BigDecimal("5000.00");
        final var expectedActive = false;

        final var updatedAtBefore = product.getUpdatedAt();

        // When
        product.update(
                expectedName,
                expectedDescription,
                expectedCategory,
                expectedBrand,
                expectedPrice,
                expectedActive
        );

        // Then
        assertThat(product.getName()).isEqualTo(expectedName);
        assertThat(product.getDescription()).isEqualTo(expectedDescription);
        assertThat(product.getCategory()).isEqualTo(expectedCategory);
        assertThat(product.getBrand()).isEqualTo(expectedBrand);
        assertThat(product.getPrice()).isEqualTo(expectedPrice);
        assertThat(product.isActive()).isFalse();
        assertThat(product.getUpdatedAt()).isAfterOrEqualTo(updatedAtBefore);
    }

    @Test
    @DisplayName("Deve desativar um produto com sucesso")
    void givenAValidProduct_whenCallDeactivate_thenDeactivateProduct() {
        // Given
        final var product = Product.newProduct("Name", "Desc", "Cat", "Brand", new BigDecimal("10.0"), true);
        final var updatedAtBefore = product.getUpdatedAt();

        // When
        product.deactivate();

        // Then
        assertThat(product.isActive()).isFalse();
        assertThat(product.getUpdatedAt()).isAfterOrEqualTo(updatedAtBefore);
    }

    @Test
    @DisplayName("Deve lancar erro ao instanciar produto com nome vazio (Fail Fast)")
    void givenAnInvalidNullName_whenCallNewProduct_thenShouldThrowDomainException() {
        // Given
        final String expectedName = null;

        // When/Then
        assertThatThrownBy(() -> Product.newProduct(expectedName, "Desc", "Cat", "Brand", new BigDecimal("10.0"), true))
                .isInstanceOf(DomainException.class)
                .hasMessage("'name' should not be empty or null");
    }

    @Test
    @DisplayName("Deve reconstruir um produto via with()")
    void givenValidParams_whenCallWith_thenReconstituteProduct() {
        // Given
        final var expectedId = ProductID.unique();
        final var expectedName = "Notebook";
        final var expectedDescription = "Notebook de alta performance";
        final var expectedCategory = "Eletronicos";
        final var expectedBrand = "Dell";
        final var expectedPrice = new BigDecimal("5000.00");
        final var expectedActive = true;
        final var now = LocalDateTime.now();

        // When
        final var actualProduct = Product.with(
                expectedId,
                expectedName,
                expectedDescription,
                expectedCategory,
                expectedBrand,
                expectedPrice,
                expectedActive,
                now,
                now
        );

        // Then
        assertThat(actualProduct)
                .isNotNull()
                .returns(expectedId, Product::getId)
                .returns(expectedName, Product::getName)
                .returns(expectedDescription, Product::getDescription)
                .returns(expectedCategory, Product::getCategory)
                .returns(expectedBrand, Product::getBrand)
                .returns(expectedPrice, Product::getPrice)
                .returns(true, Product::isActive)
                .returns(now, Product::getCreatedAt)
                .returns(now, Product::getUpdatedAt);
    }
}