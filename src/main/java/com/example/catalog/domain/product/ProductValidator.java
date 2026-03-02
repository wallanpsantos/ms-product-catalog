package com.example.catalog.domain.product;

import com.example.catalog.domain.validation.Error;
import com.example.catalog.domain.validation.ValidationHandler;
import com.example.catalog.domain.validation.Validator;

import java.math.BigDecimal;

/**
 * Validador específico para a entidade {@link Product}.
 * <p>
 * Implementa as regras de validação de negócio que podem ser acumuladas.
 * Segue o <strong>Notification Pattern</strong>: em vez de lançar exceção no primeiro erro,
 * ele verifica todas as regras e reporta uma lista completa de violações.
 * </p>
 * <p>
 * <strong>Regras validadas:</strong>
 * <ul>
 *   <li>Nome: Obrigatório, máx 255 caracteres.</li>
 *   <li>Descrição: Obrigatória, máx 4000 caracteres.</li>
 *   <li>Preço: Obrigatório, maior que zero.</li>
 *   <li>Categoria e Marca: Obrigatórias.</li>
 * </ul>
 * </p>
 */
public class ProductValidator extends Validator {

    private static final int NAME_MAX_LENGTH = 255;
    private static final int DESCRIPTION_MAX_LENGTH = 4000;

    private final Product product;

    public ProductValidator(final Product product, final ValidationHandler handler) {
        super(handler);
        this.product = product;
    }

    @Override
    public void validate() {
        checkNameConstraints();
        checkDescriptionConstraints();
        checkCategoryConstraints();
        checkBrandConstraints();
        checkPriceConstraints();
    }

    private void checkNameConstraints() {
        final var name = product.getName();
        if (name == null || name.isBlank()) {
            validationHandler().append(new Error("'name' should not be empty"));
            return;
        }
        if (name.trim().length() > NAME_MAX_LENGTH) {
            validationHandler().append(new Error("'name' must be between 1 and %d characters".formatted(NAME_MAX_LENGTH)));
        }
    }

    private void checkDescriptionConstraints() {
        final var description = product.getDescription();
        if (description == null || description.isBlank()) {
            validationHandler().append(new Error("'description' should not be empty"));
            return;
        }
        if (description.trim().length() > DESCRIPTION_MAX_LENGTH) {
            validationHandler().append(new Error("'description' must be at most %d characters".formatted(DESCRIPTION_MAX_LENGTH)));
        }
    }

    private void checkCategoryConstraints() {
        if (product.getCategory() == null || product.getCategory().isBlank()) {
            validationHandler().append(new Error("'category' should not be empty"));
        }
    }

    private void checkBrandConstraints() {
        if (product.getBrand() == null || product.getBrand().isBlank()) {
            validationHandler().append(new Error("'brand' should not be empty"));
        }
    }

    private void checkPriceConstraints() {
        if (product.getPrice() == null) {
            validationHandler().append(new Error("'price' should not be null"));
            return;
        }
        if (product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            validationHandler().append(new Error("'price' must be greater than zero"));
        }
    }
}
