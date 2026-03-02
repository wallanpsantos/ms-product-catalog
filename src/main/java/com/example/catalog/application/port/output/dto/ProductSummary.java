package com.example.catalog.application.port.output.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de Projeção para consultas otimizadas.
 * <p>
 * Representa uma visão resumida (ou completa, mas desacoplada) do produto para fins de leitura.
 * Utilizado para implementar o padrão CQRS, permitindo que consultas bypassem a construção
 * da Entidade de Domínio rica {@link com.example.catalog.domain.product.Product}.
 * </p>
 */
public record ProductSummary(
        String id,
        String name,
        String description,
        String category,
        String brand,
        BigDecimal price,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
