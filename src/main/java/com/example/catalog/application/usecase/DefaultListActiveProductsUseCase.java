package com.example.catalog.application.usecase;

import com.example.catalog.application.port.input.ListActiveProductsUseCase;
import com.example.catalog.application.port.output.ProductQueryGateway;
import com.example.catalog.application.port.output.dto.ProductSummary;
import com.example.catalog.domain.pagination.Pagination;
import com.example.catalog.domain.pagination.SearchQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Implementação padrão de {@link ListActiveProductsUseCase}.
 * <p>
 * Atua como um <strong>Query Handler</strong> no padrão CQRS.
 * Recupera projeções paginadas (Domain Bypass) diretamente da infraestrutura
 * para otimizar o tempo de resposta, sem causar efeitos colaterais (CQS).
 * </p>
 */
public class DefaultListActiveProductsUseCase extends ListActiveProductsUseCase {

    private static final Logger log = LoggerFactory.getLogger(DefaultListActiveProductsUseCase.class);

    private final ProductQueryGateway productGateway;

    public DefaultListActiveProductsUseCase(final ProductQueryGateway productGateway) {
        this.productGateway = Objects.requireNonNull(productGateway);
    }

    @Override
    public Pagination<Output> execute(final SearchQuery query) {
        log.info("Listando produtos ativos. page={}", query.page());
        final var page = productGateway.findAllActiveSummary(query).map(this::toOutput);
        log.info("Listagem de produtos ativos concluída. totalElements={}", page.total());
        return page;
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
