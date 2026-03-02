package com.example.catalog.infrastructure.adapter.input.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * DTO específico para operações de atualização em lote (Batch Update).
 * <p>
 * Diferente do {@link ProductRequest}, este record exige o campo {@code id} explicitamente,
 * pois em operações de lote não é possível passar o ID via URL (Path Variable).
 * </p>
 */
public record UpdateProductRequest(
        @NotBlank(message = "ID is required for batch update")
        String id,

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
