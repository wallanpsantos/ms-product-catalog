package com.example.catalog.infrastructure.adapter.input.rest.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de Resposta completo para um Produto.
 * <p>
 * Contém todos os dados públicos do produto, incluindo metadados de auditoria
 * (data de criação e atualização). Utilizado em operações de leitura (GET) e
 * como retorno detalhado de atualizações.
 * </p>
 */
public record ProductResponse(
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
