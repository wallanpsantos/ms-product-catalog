package com.example.catalog.infrastructure.adapter.output.gateway;

import com.example.catalog.application.port.output.ProductQueryGateway;
import com.example.catalog.application.port.output.dto.ProductSummary;
import com.example.catalog.domain.product.ProductID;
import com.example.catalog.infrastructure.adapter.output.persistence.ProductDocument;
import com.example.catalog.infrastructure.adapter.output.persistence.ProductMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Adaptador de Persistência focado em LEITURA (Query Side).
 * <p>
 * Implementa o padrão Domain Bypass ao converter documentos do MongoDB diretamente
 * para DTOs de leitura, evitando a sobrecarga de carregar Agregados de Domínio.
 * </p>
 */
@Component
public class ProductMongoQueryAdapter implements ProductQueryGateway {

    private static final Logger log = LoggerFactory.getLogger(ProductMongoQueryAdapter.class);

    private final ProductMongoRepository repository;
    private final MongoTemplate mongoTemplate;

    public ProductMongoQueryAdapter(final ProductMongoRepository repository, final MongoTemplate mongoTemplate) {
        this.repository = Objects.requireNonNull(repository);
        this.mongoTemplate = Objects.requireNonNull(mongoTemplate);
    }

    @Override
    public Optional<ProductSummary> findSummaryById(final ProductID id) {
        log.info("action=persistFindSummaryById id={}", id.getValue());
        return repository.findById(id.getValue()).map(this::toSummary);
    }

    @Override
    public Page<ProductSummary> findAllActiveSummary(final Pageable pageable) {
        log.info("action=persistFindAllActiveSummary page={}", pageable.getPageNumber());
        return repository.findAllByActiveTrue(pageable).map(this::toSummary);
    }

    @Override
    public List<ProductSummary> searchProductsSummary(final String query) {
        log.info("action=persistSearchProductsSummary term={}", query);
        final var results = performSearch(query).stream()
                .map(this::toSummary)
                .toList();
        log.info("action=persistSearchProductsSummary term={} resultsCount={}", query, results.size());
        return results;
    }

    private List<ProductDocument> performSearch(final String query) {
        final var pattern = Pattern.compile(query, Pattern.CASE_INSENSITIVE);

        final var textCriteria = new Criteria().orOperator(
                Criteria.where("name").regex(pattern),
                Criteria.where("description").regex(pattern),
                Criteria.where("category").regex(pattern),
                Criteria.where("brand").regex(pattern)
        );
        final var criteria = new Criteria().andOperator(
                Criteria.where("active").is(true),
                textCriteria
        );

        return mongoTemplate.find(new Query(criteria), ProductDocument.class);
    }

    private ProductSummary toSummary(final ProductDocument doc) {
        return new ProductSummary(
                doc.getId(),
                doc.getName(),
                doc.getDescription(),
                doc.getCategory(),
                doc.getBrand(),
                doc.getPrice(),
                doc.getActive() != null ? doc.getActive() : false,
                doc.getCreatedAt(),
                doc.getUpdatedAt()
        );
    }
}
