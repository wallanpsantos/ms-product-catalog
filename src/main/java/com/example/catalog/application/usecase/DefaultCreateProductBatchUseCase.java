package com.example.catalog.application.usecase;

import com.example.catalog.application.port.input.CreateProductBatchUseCase;
import com.example.catalog.application.port.input.CreateProductUseCase;
import com.example.catalog.application.port.output.ProductCommandGateway;
import com.example.catalog.domain.exception.NotificationException;
import com.example.catalog.domain.product.Product;
import com.example.catalog.domain.validation.handler.Notification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Implementação padrão de {@link CreateProductBatchUseCase}.
 * <p>
 * Atua como um <strong>Command Handler</strong> no padrão CQRS.
 * Processa a criação em lote de múltiplos produtos, garantindo que as regras
 * de negócio (validações do domínio) sejam aplicadas a cada item antes da persistência.
 * Retorna apenas os identificadores gerados, aderindo ao princípio CQS.
 * </p>
 */
public class DefaultCreateProductBatchUseCase extends CreateProductBatchUseCase {

    private final ProductCommandGateway productGateway;

    public DefaultCreateProductBatchUseCase(final ProductCommandGateway productGateway) {
        this.productGateway = Objects.requireNonNull(productGateway);
    }

    @Override
    public List<CreateProductUseCase.Output> execute(final List<CreateProductUseCase.Input> inputs) {
        final var productsToCreate = new ArrayList<Product>();

        for (int i = 0; i < inputs.size(); i++) {
            final var input = inputs.get(i);
            final var notification = Notification.create();

            final var product = Product.newProduct(
                    input.name(), input.description(), input.category(),
                    input.brand(), input.price(), input.active() != null ? input.active() : Boolean.TRUE
            );

            product.validate(notification);

            if (notification.hasError()) {
                throw NotificationException.with("Erro de validação no item do lote índice: " + i, notification);
            }
            productsToCreate.add(product);
        }

        final var savedProducts = productGateway.createAll(productsToCreate);

        return savedProducts.stream()
                .map(p -> new CreateProductUseCase.Output(p.getId().getValue()))
                .toList();
    }
}
