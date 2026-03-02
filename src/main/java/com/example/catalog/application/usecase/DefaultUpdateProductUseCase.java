package com.example.catalog.application.usecase;

import com.example.catalog.application.port.input.UpdateProductUseCase;
import com.example.catalog.application.port.output.ProductCommandGateway;
import com.example.catalog.domain.exception.NotFoundException;
import com.example.catalog.domain.exception.NotificationException;
import com.example.catalog.domain.product.Product;
import com.example.catalog.domain.product.ProductID;
import com.example.catalog.domain.validation.handler.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Implementação padrão de {@link UpdateProductUseCase}.
 * <p>
 * Atua como um <strong>Command Handler</strong> no padrão CQRS.
 * Carrega a Raiz de Agregado e orquestra a alteração de seu estado garantindo
 * todas as invariantes e validações de domínio.
 * Retorna estritamente o identificador gerado, de acordo com o CQS.
 * </p>
 */
public class DefaultUpdateProductUseCase extends UpdateProductUseCase {

    private static final Logger log = LoggerFactory.getLogger(DefaultUpdateProductUseCase.class);

    private final ProductCommandGateway productGateway;

    public DefaultUpdateProductUseCase(final ProductCommandGateway productGateway) {
        this.productGateway = Objects.requireNonNull(productGateway);
    }

    @Override
    public Output execute(final Input input) {
        log.info("Iniciando atualização de produto. id={}", input.id());

        final var productID = ProductID.from(input.id());
        final var product = productGateway.findById(productID)
                .orElseThrow(() -> NotFoundException.with(Product.class, input.id()));

        final var notification = Notification.create();
        product.update(input.name(), input.description(), input.category(),
                input.brand(), input.price(), input.active());

        product.validate(notification);

        if (notification.hasError()) {
            throw NotificationException.with("Não foi possível atualizar o Agregado de Produto", notification);
        }

        final var updated = productGateway.update(product);
        log.info("Produto atualizado com sucesso. id={}", input.id());
        return new Output(updated.getId().getValue());
    }
}
