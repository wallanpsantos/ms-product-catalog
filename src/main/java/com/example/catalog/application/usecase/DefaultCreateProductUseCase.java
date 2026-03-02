package com.example.catalog.application.usecase;

import com.example.catalog.application.port.input.CreateProductUseCase;
import com.example.catalog.application.port.output.ProductCommandGateway;
import com.example.catalog.domain.exception.NotificationException;
import com.example.catalog.domain.product.Product;
import com.example.catalog.domain.validation.handler.Notification;

import java.util.Objects;

/**
 * Implementação padrão de {@link CreateProductUseCase}.
 * <p>
 * Atua como um <strong>Command Handler</strong> no padrão CQRS.
 * Orquestra a criação de um novo Produto, validando as regras de negócio ricas
 * do domínio e garantindo a consistência do estado antes de persistir.
 * Retorna exclusivamente o ID gerado, alinhando-se ao princípio CQS.
 * </p>
 */
public class DefaultCreateProductUseCase extends CreateProductUseCase {

    private final ProductCommandGateway productGateway;

    public DefaultCreateProductUseCase(final ProductCommandGateway productGateway) {
        this.productGateway = Objects.requireNonNull(productGateway);
    }

    @Override
    public Output execute(final Input input) {
        final var notification = Notification.create();
        final var product = Product.newProduct(
                input.name(), input.description(), input.category(),
                input.brand(), input.price(), input.active() != null ? input.active() : Boolean.TRUE
        );

        product.validate(notification);

        if (notification.hasError()) {
            throw NotificationException.with("Não foi possível criar o Agregado de Produto", notification);
        }

        final var saved = productGateway.create(product);
        return new Output(saved.getId().getValue());
    }
}
