package com.example.catalog.infrastructure.adapter.input.rest.mapper;

import com.example.catalog.application.port.input.CreateProductUseCase;
import com.example.catalog.application.port.input.GetProductByIdUseCase;
import com.example.catalog.application.port.input.ListActiveProductsUseCase;
import com.example.catalog.application.port.input.SearchProductsUseCase;
import com.example.catalog.application.port.input.UpdateProductUseCase;
import com.example.catalog.infrastructure.adapter.input.rest.dto.request.ProductRequest;
import com.example.catalog.infrastructure.adapter.input.rest.dto.request.UpdateProductRequest;
import com.example.catalog.infrastructure.adapter.input.rest.dto.response.ProductResponse;
import org.springframework.stereotype.Component;

/**
 * Componente responsável pelo mapeamento entre DTOs da API REST e objetos de Entrada/Saída dos Casos de Uso.
 * <p>
 * Este mapeador garante o desacoplamento: impede que objetos da camada de infraestrutura
 * (como {@link ProductRequest}) vazem para dentro da camada de aplicação, e vice-versa.
 * </p>
 */
@Component
public class ProductRestMapper {

    public CreateProductUseCase.Input toCreateInput(final ProductRequest request) {
        return new CreateProductUseCase.Input(
                request.name(), request.description(), request.category(),
                request.brand(), request.price(), Boolean.TRUE.equals(request.active())
        );
    }

    public UpdateProductUseCase.Input toUpdateInput(final String id, final ProductRequest request) {
        return new UpdateProductUseCase.Input(
                id, request.name(), request.description(), request.category(),
                request.brand(), request.price(), Boolean.TRUE.equals(request.active())
        );
    }

    public UpdateProductUseCase.Input toUpdateInput(final UpdateProductRequest request) {
        return new UpdateProductUseCase.Input(
                request.id(), request.name(), request.description(), request.category(),
                request.brand(), request.price(), Boolean.TRUE.equals(request.active())
        );
    }

    public ProductResponse toResponse(final GetProductByIdUseCase.Output output) {
        return new ProductResponse(output.id(), output.name(), output.description(),
                output.category(), output.brand(), output.price(),
                output.active(), output.createdAt(), output.updatedAt());
    }

    public ProductResponse toResponse(final ListActiveProductsUseCase.Output output) {
        return new ProductResponse(output.id(), output.name(), output.description(),
                output.category(), output.brand(), output.price(),
                output.active(), output.createdAt(), output.updatedAt());
    }

    public ProductResponse toResponse(final SearchProductsUseCase.Output output) {
        return new ProductResponse(output.id(), output.name(), output.description(),
                output.category(), output.brand(), output.price(),
                output.active(), output.createdAt(), output.updatedAt());
    }
}
