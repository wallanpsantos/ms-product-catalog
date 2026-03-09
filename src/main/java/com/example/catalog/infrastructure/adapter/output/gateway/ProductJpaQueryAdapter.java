package com.example.catalog.infrastructure.adapter.output.gateway;

import com.example.catalog.application.port.output.ProductQueryGateway;
import com.example.catalog.application.port.output.dto.ProductSummary;
import com.example.catalog.domain.pagination.Pagination;
import com.example.catalog.domain.pagination.SearchQuery;
import com.example.catalog.domain.product.ProductID;
import com.example.catalog.infrastructure.adapter.output.persistence.ProductJpaEntity;
import com.example.catalog.infrastructure.adapter.output.persistence.ProductJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Adaptador de Persistência focado em LEITURA (Query Side).
 * <p>
 * Implementa o padrão Domain Bypass ao converter entidades do JPA diretamente
 * para DTOs de leitura, evitando a sobrecarga de carregar Agregados de Domínio.
 * </p>
 */
@Component
public class ProductJpaQueryAdapter implements ProductQueryGateway {

    private static final Logger log = LoggerFactory.getLogger(ProductJpaQueryAdapter.class);

    private final ProductJpaRepository repository;

    public ProductJpaQueryAdapter(final ProductJpaRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    @Override
    public Optional<ProductSummary> findSummaryById(final ProductID id) {
        log.info("action=persistFindSummaryById id={}", id.getValue());
        return repository.findById(id.getValue()).map(this::toSummary);
    }

    @Override
    public Pagination<ProductSummary> findAllActiveSummary(final SearchQuery query) {
        log.info("action=persistFindAllActiveSummary page={}", query.page());
        final var sortDir = "desc".equalsIgnoreCase(query.direction()) ? Sort.Direction.DESC : Sort.Direction.ASC;
        final var pageRequest = PageRequest.of(query.page(), query.perPage(), Sort.by(sortDir, query.sort()));

        final var pageResult = repository.findAllByActiveTrue(pageRequest);

        return new Pagination<>(
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.map(this::toSummary).toList()
        );
    }

    @Override
    public List<ProductSummary> searchProductsSummary(final String query) {
        log.info("action=persistSearchProductsSummary term={}", query);
        final var results = repository.searchActiveProducts(query).stream()
                .map(this::toSummary)
                .toList();
        log.info("action=persistSearchProductsSummary term={} resultsCount={}", query, results.size());
        return results;
    }

    private ProductSummary toSummary(final ProductJpaEntity entity) {
        return new ProductSummary(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getCategory(),
                entity.getBrand(),
                entity.getPrice(),
                entity.getActive() != null ? entity.getActive() : Boolean.FALSE,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
