package com.example.catalog.application.usecase;

import com.example.catalog.application.port.input.DeactivateProductUseCase;
import com.example.catalog.application.port.output.ProductCommandGateway;
import com.example.catalog.domain.exception.NotFoundException;
import com.example.catalog.domain.product.Product;
import com.example.catalog.domain.product.ProductID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Implementação padrão de {@link DeactivateProductUseCase}.
 * <p>
 * Atua como um <strong>Command Handler</strong> no padrão CQRS.
 * Encarregado de alterar o estado do Produto para inativo (soft delete).
 * Segue estritamente o CQS, não retornando nenhum dado de consulta.
 * </p>
 */
public class DefaultDeactivateProductUseCase extends DeactivateProductUseCase {

    private static final Logger log = LoggerFactory.getLogger(DefaultDeactivateProductUseCase.class);

    private final ProductCommandGateway productGateway;

    public DefaultDeactivateProductUseCase(final ProductCommandGateway productGateway) {
        this.productGateway = Objects.requireNonNull(productGateway);
    }

    @Override
    public Output execute(final Input input) {
        log.info("Iniciando desativação de produto. id={}", input.id());

        final var product = productGateway.findById(ProductID.from(input.id()))
                .orElseThrow(() -> NotFoundException.with(Product.class, input.id()));

        product.deactivate();
        productGateway.update(product);

        log.info("Produto desativado com sucesso. id={}", input.id());

        return new Output();
    }
}
