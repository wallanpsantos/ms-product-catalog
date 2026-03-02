package com.example.catalog.application.usecase;

import com.example.catalog.application.port.input.ListActiveProductsUseCase;
import com.example.catalog.application.port.output.ProductQueryGateway;
import com.example.catalog.application.port.output.dto.ProductSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
    public Page<Output> execute(final Pageable pageable) {
        log.info("Listando produtos ativos. page={}", pageable.getPageNumber());
        final var page = productGateway.findAllActiveSummary(pageable).map(this::toOutput);
        log.info("Listagem de produtos ativos concluída. totalElements={}", page.getTotalElements());
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
