package com.example.catalog.infrastructure.adapter.input.rest.dto.request;

import com.example.catalog.application.port.input.CreateProductUseCase;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * DTO (Data Transfer Object) de Entrada para criação e atualização de produtos.
 * <p>
 * Implementa a interface de Entrada {@link CreateProductUseCase.Input} diretamente,
 * eliminando a necessidade de mapeamento explícito na camada de aplicação.
 * Utiliza anotações do Jakarta Validation (`@NotBlank`, `@Positive`) para garantir
 * que os dados recebidos estejam semanticamente corretos antes de serem processados.
 * </p>
 */
public record ProductRequest(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Description is required")
        String description,

        @NotBlank(message = "Category is required")
        String category,

        @NotBlank(message = "Brand is required")
        String brand,

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be positive")
        BigDecimal price,

        @NotNull(message = "Active flag is required")
        Boolean active
) {
}
