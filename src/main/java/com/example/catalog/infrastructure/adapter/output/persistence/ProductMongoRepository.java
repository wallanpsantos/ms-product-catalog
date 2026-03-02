package com.example.catalog.infrastructure.adapter.output.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repositório Spring Data MongoDB para operações CRUD básicas.
 * <p>
 * Atua como a interface de baixo nível para persistência de documentos {@link ProductDocument}.
 * Consultas complexas, paginação customizada e critérios dinâmicos são delegados para
 * os adaptadores {@link com.example.catalog.infrastructure.adapter.output.gateway.ProductMongoCommandAdapter}
 * e {@link com.example.catalog.infrastructure.adapter.output.gateway.ProductMongoQueryAdapter}
 * via {@code MongoTemplate}.
 * </p>
 */
public interface ProductMongoRepository extends MongoRepository<ProductDocument, String> {

    Page<ProductDocument> findAllByActiveTrue(Pageable pageable);
}
