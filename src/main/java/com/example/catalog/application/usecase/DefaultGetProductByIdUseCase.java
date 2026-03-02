package com.example.catalog.application.usecase;

import com.example.catalog.application.port.input.GetProductByIdUseCase;
import com.example.catalog.application.port.output.ProductQueryGateway;
import com.example.catalog.application.port.output.dto.ProductSummary;
import com.example.catalog.domain.exception.NotFoundException;
import com.example.catalog.domain.product.Product;
import com.example.catalog.domain.product.ProductID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Implementação padrão de {@link GetProductByIdUseCase}.
 * <p>
 * Atua como um <strong>Query Handler</strong> no padrão CQRS.
 * Sua responsabilidade é estritamente de leitura, recuperando DTOs de projeção
 * (Domain Bypass) diretamente da infraestrutura para otimizar o tempo de resposta,
 * sem instanciar entidades de domínio ricas e sem causar efeitos colaterais (CQS).
 * </p>
 */
public class DefaultGetProductByIdUseCase extends GetProductByIdUseCase {

    private static final Logger log = LoggerFactory.getLogger(DefaultGetProductByIdUseCase.class);

    private final ProductQueryGateway productGateway;

    public DefaultGetProductByIdUseCase(final ProductQueryGateway productGateway) {
        this.productGateway = Objects.requireNonNull(productGateway);
    }

    @Override
    public Output execute(final Input input) {
        log.info("Buscando produto por ID. id={}", input.id());

        return productGateway.findSummaryById(ProductID.from(input.id()))
                .map(this::toOutput)
                .orElseThrow(() -> NotFoundException.with(Product.class, input.id()));
    }

    private Output toOutput(final ProductSummary summary) {
        return new Output(
                summary.id(),
                summary.name(),
                summary.description(),
                summary.category(),
                summary.brand(),
                summary.price(),
                summary.active(),
                summary.createdAt(),
                summary.updatedAt()
        );
    }
}
