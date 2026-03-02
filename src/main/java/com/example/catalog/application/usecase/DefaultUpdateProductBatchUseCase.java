package com.example.catalog.application.usecase;

import com.example.catalog.application.port.input.UpdateProductBatchUseCase;
import com.example.catalog.application.port.input.UpdateProductUseCase;
import com.example.catalog.application.port.output.ProductCommandGateway;
import com.example.catalog.domain.exception.NotFoundException;
import com.example.catalog.domain.exception.NotificationException;
import com.example.catalog.domain.product.Product;
import com.example.catalog.domain.product.ProductID;
import com.example.catalog.domain.validation.handler.Notification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Implementação padrão de {@link UpdateProductBatchUseCase}.
 * <p>
 * Atua como um <strong>Command Handler</strong> no padrão CQRS.
 * Processa a atualização em lote de múltiplos produtos, aplicando regras
 * de negócio e validações ricas antes da persistência. Retorna apenas
 * os IDs atualizados (CQS).
 * </p>
 */
public class DefaultUpdateProductBatchUseCase extends UpdateProductBatchUseCase {

    private final ProductCommandGateway productGateway;

    public DefaultUpdateProductBatchUseCase(final ProductCommandGateway productGateway) {
        this.productGateway = Objects.requireNonNull(productGateway);
    }

    @Override
    public List<UpdateProductUseCase.Output> execute(final List<UpdateProductUseCase.Input> inputs) {
        final var ids = inputs.stream()
                .map(input -> ProductID.from(input.id()))
                .toList();

        final var existingProducts = productGateway.findAllById(ids);

        if (existingProducts.size() != ids.size()) {
            final var foundIds = existingProducts.stream()
                    .map(Product::getId)
                    .map(ProductID::getValue)
                    .collect(Collectors.toSet());

            final var missingIds = inputs.stream()
                    .map(UpdateProductUseCase.Input::id)
                    .filter(id -> !foundIds.contains(id))
                    .collect(Collectors.joining(", "));

            throw NotFoundException.with(Product.class, missingIds);
        }

        final Map<String, Product> productMap = existingProducts.stream()
                .collect(Collectors.toMap(p -> p.getId().getValue(), Function.identity()));

        final var productsToUpdate = new ArrayList<Product>();

        for (int i = 0; i < inputs.size(); i++) {
            final var input = inputs.get(i);
            final var product = productMap.get(input.id());

            final var notification = Notification.create();
            product.update(
                    input.name(), input.description(), input.category(),
                    input.brand(), input.price(), input.active()
            );

            product.validate(notification);

            if (notification.hasError()) {
                throw NotificationException.with("Erro de validação no item do lote índice: " + i, notification);
            }
            productsToUpdate.add(product);
        }

        final var savedProducts = productGateway.updateAll(productsToUpdate);

        return savedProducts.stream()
                .map(p -> new UpdateProductUseCase.Output(p.getId().getValue()))
                .toList();
    }
}
