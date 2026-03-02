package com.example.catalog.infrastructure.adapter.input.rest.controller;

import com.example.catalog.application.port.input.CreateProductBatchUseCase;
import com.example.catalog.application.port.input.CreateProductUseCase;
import com.example.catalog.application.port.input.DeactivateProductUseCase;
import com.example.catalog.application.port.input.UpdateProductBatchUseCase;
import com.example.catalog.application.port.input.UpdateProductUseCase;
import com.example.catalog.infrastructure.adapter.input.rest.ProductCommandApi;
import com.example.catalog.infrastructure.adapter.input.rest.dto.request.ProductRequest;
import com.example.catalog.infrastructure.adapter.input.rest.dto.request.SearchRequest;
import com.example.catalog.infrastructure.adapter.input.rest.dto.request.UpdateProductRequest;
import com.example.catalog.infrastructure.adapter.input.rest.dto.response.CreateProductResponse;
import com.example.catalog.infrastructure.adapter.input.rest.dto.response.ProductResponse;
import com.example.catalog.infrastructure.adapter.input.rest.mapper.ProductRestMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.Objects;

/**
 * Controlador REST exclusivo para **escrita e mutação** de dados de Produtos.
 * <p>
 * <strong>Por que este padrão?</strong><br>
 * Esta classe representa o <strong>lado de Escrita (Command)</strong> da arquitetura <strong>CQRS</strong>.
 * Trabalhando em conjunto com o {@link ProductQueryController}, ela aplica o princípio <strong>CQS</strong> ao agrupar
 * unicamente métodos que alteram o estado do sistema (POST, PUT, DELETE).
 * Ela não expõe métodos de leitura, reduzindo o acoplamento, limitando as injeções de dependência
 * e mantendo a classe coesa e focada estritamente em transações e regras de negócio.
 * </p>
 */
@RestController
public class ProductCommandController implements ProductCommandApi {

    private final CreateProductUseCase createProductUseCase;
    private final CreateProductBatchUseCase createProductBatchUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final UpdateProductBatchUseCase updateProductBatchUseCase;
    private final DeactivateProductUseCase deactivateProductUseCase;
    private final ProductRestMapper mapper;

    public ProductCommandController(
            final CreateProductUseCase createProductUseCase,
            final CreateProductBatchUseCase createProductBatchUseCase,
            final UpdateProductUseCase updateProductUseCase,
            final UpdateProductBatchUseCase updateProductBatchUseCase,
            final DeactivateProductUseCase deactivateProductUseCase,
            final ProductRestMapper mapper
    ) {
        this.createProductUseCase = Objects.requireNonNull(createProductUseCase);
        this.createProductBatchUseCase = Objects.requireNonNull(createProductBatchUseCase);
        this.updateProductUseCase = Objects.requireNonNull(updateProductUseCase);
        this.updateProductBatchUseCase = Objects.requireNonNull(updateProductBatchUseCase);
        this.deactivateProductUseCase = Objects.requireNonNull(deactivateProductUseCase);
        this.mapper = Objects.requireNonNull(mapper);
    }

    @Override
    public ResponseEntity<CreateProductResponse> createProduct(final ProductRequest input) {
        final var output = createProductUseCase.execute(mapper.toCreateInput(input));
        return ResponseEntity.created(URI.create("/api/v1/products/" + output.id()))
                .body(new CreateProductResponse(output.id()));
    }

    @Override
    public ResponseEntity<List<CreateProductResponse>> createProductBatch(final List<ProductRequest> input) {
        final var useCaseInputs = input.stream().map(mapper::toCreateInput).toList();
        final var output = createProductBatchUseCase.execute(useCaseInputs);

        final var response = output.stream()
                .map(o -> new CreateProductResponse(o.id()))
                .toList();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    public ResponseEntity<ProductResponse> getById(final String id) {
        throw new UnsupportedOperationException("Queries are handled by ProductQueryController");
    }

    @Override
    public ResponseEntity<CreateProductResponse> updateProduct(final String id, final ProductRequest input) {
        final var output = updateProductUseCase.execute(mapper.toUpdateInput(id, input));
        return ResponseEntity.ok(new CreateProductResponse(output.id()));
    }

    @Override
    public ResponseEntity<List<CreateProductResponse>> updateProductBatch(final List<UpdateProductRequest> input) {
        final var useCaseInputs = input.stream().map(mapper::toUpdateInput).toList();
        final var output = updateProductBatchUseCase.execute(useCaseInputs);

        final var response = output.stream()
                .map(o -> new CreateProductResponse(o.id()))
                .toList();

        return ResponseEntity.ok(response);
    }

    @Override
    public void deleteById(final String id) {
        deactivateProductUseCase.execute(new DeactivateProductUseCase.Input(id));
    }

    public ResponseEntity<Page<ProductResponse>> listProducts(int page, int perPage, String sort, String direction) {
        throw new UnsupportedOperationException("Queries are handled by ProductQueryController");
    }

    public ResponseEntity<List<ProductResponse>> searchProducts(SearchRequest input) {
        throw new UnsupportedOperationException("Queries are handled by ProductQueryController");
    }
}
