package com.example.catalog.infrastructure.adapter.output.gateway;

import com.example.catalog.application.port.output.ProductCommandGateway;
import com.example.catalog.domain.product.Product;
import com.example.catalog.domain.product.ProductID;
import com.example.catalog.infrastructure.adapter.output.persistence.ProductDocument;
import com.example.catalog.infrastructure.adapter.output.persistence.ProductMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Adaptador de Persistência focado em ESCRITA (Command Side).
 * <p>
 * Responsável por traduzir as intenções de mudança de estado do domínio para
 * operações de banco de dados (MongoDB). Implementa estritamente o {@link ProductCommandGateway}.
 * </p>
 */
@Component
public class ProductMongoCommandAdapter implements ProductCommandGateway {

    private static final Logger log = LoggerFactory.getLogger(ProductMongoCommandAdapter.class);

    private final ProductMongoRepository repository;

    public ProductMongoCommandAdapter(final ProductMongoRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    @Override
    public Product create(final Product product) {
        log.info("action=persistCreate id={}", product.getId().getValue());
        final var saved = repository.save(ProductDocument.from(product));
        return saved.toEntity();
    }

    @Override
    public List<Product> createAll(final List<Product> products) {
        log.info("action=persistCreateAll batchSize={}", products.size());
        final var documents = products.stream()
                .map(ProductDocument::from)
                .toList();
        return repository.saveAll(documents).stream()
                .map(ProductDocument::toEntity)
                .toList();
    }

    @Override
    public Product update(final Product product) {
        log.info("action=persistUpdate id={}", product.getId().getValue());
        final var saved = repository.save(ProductDocument.from(product));
        return saved.toEntity();
    }

    @Override
    public List<Product> updateAll(final List<Product> products) {
        log.info("action=persistUpdateAll batchSize={}", products.size());
        final var documents = products.stream()
                .map(ProductDocument::from)
                .toList();
        return repository.saveAll(documents).stream()
                .map(ProductDocument::toEntity)
                .toList();
    }

    @Override
    public Optional<Product> findById(final ProductID id) {
        // Necessário para carregar o Agregado antes de aplicar mudanças (ex: update/delete)
        log.info("action=persistFindByIdForCommand id={}", id.getValue());
        return repository.findById(id.getValue()).map(ProductDocument::toEntity);
    }

    @Override
    public List<Product> findAllById(final List<ProductID> ids) {
        // Necessário para operações em lote
        log.info("action=persistFindAllByIdForCommand batchSize={}", ids.size());
        final var rawIds = ids.stream().map(ProductID::getValue).toList();
        return repository.findAllById(rawIds).stream()
                .map(ProductDocument::toEntity)
                .toList();
    }
}
