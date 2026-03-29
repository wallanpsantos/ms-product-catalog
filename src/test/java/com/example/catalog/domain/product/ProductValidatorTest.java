package com.example.catalog.domain.product;

import com.example.catalog.UnitTest;
import com.example.catalog.domain.validation.handler.Notification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
class ProductValidatorTest {

    @Test
    @DisplayName("Deve validar um produto valido sem erros")
    void givenAValidProduct_whenCallValidate_thenShouldNotHaveErrors() {
        // Given
        final var product = Product.newProduct("Notebook", "Desc", "Cat", "Brand", new BigDecimal("10.0"), true);
        final var notification = Notification.create();

        // When
        product.validate(notification);

        // Then
        assertThat(notification.hasError()).isFalse();
    }

    @Test
    @DisplayName("Deve reportar erro ao validar produto com nome muito longo")
    void givenAnInvalidNameLength_whenCallValidate_thenShouldHaveErrors() {
        // Given
        final var product = mock(Product.class);
        when(product.getName()).thenReturn("A".repeat(256));
        when(product.getDescription()).thenReturn("Desc");
        when(product.getCategory()).thenReturn("Cat");
        when(product.getBrand()).thenReturn("Brand");
        when(product.getPrice()).thenReturn(new BigDecimal("10.0"));

        final var notification = Notification.create();
        final var validator = new ProductValidator(product, notification);

        // When
        validator.validate();

        // Then
        assertThat(notification.hasError()).isTrue();
        assertThat(notification.getErrors()).hasSize(1);
        assertThat(notification.firstError().get().message()).isEqualTo("'name' must be between 1 and 255 characters");
    }

    @Test
    @DisplayName("Deve reportar erro ao validar produto com descricao muito longa")
    void givenAnInvalidDescriptionLength_whenCallValidate_thenShouldHaveErrors() {
        // Given
        final var product = mock(Product.class);
        when(product.getName()).thenReturn("Nome");
        when(product.getDescription()).thenReturn("A".repeat(4001));
        when(product.getCategory()).thenReturn("Cat");
        when(product.getBrand()).thenReturn("Brand");
        when(product.getPrice()).thenReturn(new BigDecimal("10.0"));

        final var notification = Notification.create();
        final var validator = new ProductValidator(product, notification);

        // When
        validator.validate();

        // Then
        assertThat(notification.hasError()).isTrue();
        assertThat(notification.getErrors()).hasSize(1);
        assertThat(notification.firstError().get().message()).isEqualTo("'description' must be at most 4000 characters");
    }

    @Test
    @DisplayName("Deve reportar erro ao validar produto com preco igual a zero")
    void givenAnInvalidZeroPrice_whenCallValidate_thenShouldHaveErrors() {
        // Given
        final var product = mock(Product.class);
        when(product.getName()).thenReturn("Nome");
        when(product.getDescription()).thenReturn("Desc");
        when(product.getCategory()).thenReturn("Cat");
        when(product.getBrand()).thenReturn("Brand");
        when(product.getPrice()).thenReturn(BigDecimal.ZERO);

        final var notification = Notification.create();
        final var validator = new ProductValidator(product, notification);

        // When
        validator.validate();

        // Then
        assertThat(notification.hasError()).isTrue();
        assertThat(notification.getErrors()).hasSize(1);
        assertThat(notification.firstError().get().message()).isEqualTo("'price' must be greater than zero");
    }

    @Test
    @DisplayName("Deve reportar erro ao validar produto com preco negativo")
    void givenAnInvalidNegativePrice_whenCallValidate_thenShouldHaveErrors() {
        // Given
        final var product = mock(Product.class);
        when(product.getName()).thenReturn("Nome");
        when(product.getDescription()).thenReturn("Desc");
        when(product.getCategory()).thenReturn("Cat");
        when(product.getBrand()).thenReturn("Brand");
        when(product.getPrice()).thenReturn(new BigDecimal("-1.0"));

        final var notification = Notification.create();
        final var validator = new ProductValidator(product, notification);

        // When
        validator.validate();

        // Then
        assertThat(notification.hasError()).isTrue();
        assertThat(notification.getErrors()).hasSize(1);
        assertThat(notification.firstError().get().message()).isEqualTo("'price' must be greater than zero");
    }
}