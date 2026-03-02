package com.example.catalog.infrastructure.adapter.input.rest.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de Resposta otimizado para a criação de recursos.
 * <p>
 * Retorna apenas o ID do recurso criado (padrão CQRS-Lite), encorajando o cliente
 * a realizar uma nova consulta (GET) se necessitar dos dados completos, utilizando
 * o cabeçalho `Location` da resposta HTTP.
 * </p>
 */
@Schema(description = "Response object for created Product")
public record CreateProductResponse(
        @JsonProperty("id") String id
) {
}
