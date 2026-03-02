package com.example.catalog.application.port.output;

import com.example.catalog.domain.product.Product;
import com.example.catalog.domain.product.ProductID;

import java.util.List;
import java.util.Optional;

/**
 * <strong>Write Side (Modelo de Escrita)</strong> - Porta de Saída CQRS.
 * <p>
 * Responsável exclusivamente por operações que alteram o estado do sistema (INSERT/UPDATE/DELETE).
 * </p>
 * <p>
 * <strong>Consistência do Domínio:</strong>
 * Inclui métodos como {@code findById} estritamente para carregar o Agregado {@link Product}
 * em memória para aplicar alterações de negócio e persistir a consistência.
 * Não deve ser utilizado para consultas de apresentação (Use {@link ProductQueryGateway}).
 * </p>
 */
public interface ProductCommandGateway {

    Product create(Product product);

    List<Product> createAll(List<Product> products);

    Product update(Product product);

    List<Product> updateAll(List<Product> products);

    Optional<Product> findById(ProductID id);

    List<Product> findAllById(List<ProductID> ids);
}
