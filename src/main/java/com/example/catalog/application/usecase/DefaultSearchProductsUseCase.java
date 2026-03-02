package com.example.catalog.application.usecase;

import com.example.catalog.application.port.input.SearchProductsUseCase;
import com.example.catalog.application.port.output.ProductQueryGateway;
import com.example.catalog.application.port.output.dto.ProductSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

/**
 * Implementação padrão de {@link SearchProductsUseCase}.
 * <p>
 * Atua como um <strong>Query Handler</strong> no padrão CQRS.
 * Realiza buscas textuais flexíveis recuperando DTOs de projeção (Domain Bypass).
 * Garante a ausência de efeitos colaterais conforme exigido pelo princípio CQS.
 * </p>
 */
public class DefaultSearchProductsUseCase extends SearchProductsUseCase {

    private static final Logger log = LoggerFactory.getLogger(DefaultSearchProductsUseCase.class);

    private final ProductQueryGateway productGateway;

    public DefaultSearchProductsUseCase(final ProductQueryGateway productGateway) {
        this.productGateway = Objects.requireNonNull(productGateway);
    }

    @Override
    public List<Output> execute(final Input input) {
        log.info("Buscando produtos por termo. termo={}", input.query());
        final var results = productGateway.searchProductsSummary(input.query())
                .stream().map(this::toOutput).toList();
        log.info("Busca de produtos concluída. termo={} quantidadeResultados={}", input.query(), results.size());
        return results;
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
