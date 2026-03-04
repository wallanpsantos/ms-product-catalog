package com.example.catalog.application.port.output;

import com.example.catalog.application.port.output.dto.ProductSummary;
import com.example.catalog.domain.pagination.Pagination;
import com.example.catalog.domain.pagination.SearchQuery;
import com.example.catalog.domain.product.ProductID;

import java.util.List;
import java.util.Optional;

/**
 * <strong>Read Side (Modelo de Leitura)</strong> - Porta de Saída CQRS.
 * <p>
 * Responsável exclusivamente por consultas otimizadas para apresentação (UI/API GET).
 * </p>
 * <p>
 * <strong>Otimização e Domain Bypass:</strong>
 * Esta porta trabalha exclusivamente com {@link ProductSummary} para evitar o custo
 * de reconstruir Entidades de Domínio ricas em operações de leitura pura.
 * </p>
 */
public interface ProductQueryGateway {

    Optional<ProductSummary> findSummaryById(ProductID id);

    Pagination<ProductSummary> findAllActiveSummary(SearchQuery query);

    List<ProductSummary> searchProductsSummary(String query);
}
