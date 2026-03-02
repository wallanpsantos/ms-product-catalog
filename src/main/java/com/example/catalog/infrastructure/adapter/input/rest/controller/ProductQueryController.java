package com.example.catalog.infrastructure.adapter.input.rest.controller;

import com.example.catalog.application.port.input.GetProductByIdUseCase;
import com.example.catalog.application.port.input.ListActiveProductsUseCase;
import com.example.catalog.application.port.input.SearchProductsUseCase;
import com.example.catalog.infrastructure.adapter.input.rest.ProductQueryApi;
import com.example.catalog.infrastructure.adapter.input.rest.dto.request.ProductRequest;
import com.example.catalog.infrastructure.adapter.input.rest.dto.request.SearchRequest;
import com.example.catalog.infrastructure.adapter.input.rest.dto.request.UpdateProductRequest;
import com.example.catalog.infrastructure.adapter.input.rest.dto.response.CreateProductResponse;
import com.example.catalog.infrastructure.adapter.input.rest.dto.response.ProductResponse;
import com.example.catalog.infrastructure.adapter.input.rest.mapper.ProductRestMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

/**
 * Controlador REST exclusivo para **leitura e consultas** de Produtos.
 * <p>
 * <strong>Por que este padrão?</strong><br>
 * Esta classe representa o <strong>lado de Leitura (Query)</strong> da arquitetura <strong>CQRS</strong>.
 * Trabalhando em conjunto com o {@link ProductCommandController}, ela aplica o princípio <strong>CQS</strong> ao agrupar
 * unicamente métodos que recuperam dados (GET, buscas). Isso permite utilizar projeções otimizadas (DTOs)
 * sem a sobrecarga do domínio rico (Domain Bypass), garantindo que endpoints de consulta sejam rápidos
 * e seguros contra efeitos colaterais.
 * </p>
 */
@RestController
public class ProductQueryController implements ProductQueryApi {

    private final GetProductByIdUseCase getProductByIdUseCase;
    private final ListActiveProductsUseCase listActiveProductsUseCase;
    private final SearchProductsUseCase searchProductsUseCase;
    private final ProductRestMapper mapper;

    public ProductQueryController(
            final GetProductByIdUseCase getProductByIdUseCase,
            final ListActiveProductsUseCase listActiveProductsUseCase,
            final SearchProductsUseCase searchProductsUseCase,
            final ProductRestMapper mapper
    ) {
        this.getProductByIdUseCase = Objects.requireNonNull(getProductByIdUseCase);
        this.listActiveProductsUseCase = Objects.requireNonNull(listActiveProductsUseCase);
        this.searchProductsUseCase = Objects.requireNonNull(searchProductsUseCase);
        this.mapper = Objects.requireNonNull(mapper);
    }

    public ResponseEntity<CreateProductResponse> createProduct(ProductRequest input) {
        throw new UnsupportedOperationException("Commands are handled by ProductCommandController");
    }

    public ResponseEntity<List<CreateProductResponse>> createProductBatch(List<ProductRequest> input) {
        throw new UnsupportedOperationException("Commands are handled by ProductCommandController");
    }

    @Override
    public ResponseEntity<ProductResponse> getById(final String id) {
        final var output = getProductByIdUseCase.execute(new GetProductByIdUseCase.Input(id));
        return ResponseEntity.ok(mapper.toResponse(output));
    }

    public ResponseEntity<CreateProductResponse> updateProduct(String id, ProductRequest input) {
        throw new UnsupportedOperationException("Commands are handled by ProductCommandController");
    }

    public ResponseEntity<List<CreateProductResponse>> updateProductBatch(List<UpdateProductRequest> input) {
        throw new UnsupportedOperationException("Commands are handled by ProductCommandController");
    }

    public void deleteById(String id) {
        throw new UnsupportedOperationException("Commands are handled by ProductCommandController");
    }

    @Override
    public ResponseEntity<Page<ProductResponse>> listProducts(
            final int page,
            final int perPage,
            final String sort,
            final String direction
    ) {
        final var sortDir = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        final var pageRequest = PageRequest.of(page, perPage, Sort.by(sortDir, sort));

        final var result = listActiveProductsUseCase.execute(pageRequest)
                .map(mapper::toResponse);
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<List<ProductResponse>> searchProducts(final SearchRequest input) {
        final var results = searchProductsUseCase.execute(new SearchProductsUseCase.Input(input.query()))
                .stream()
                .map(mapper::toResponse)
                .toList();
        return ResponseEntity.ok(results);
    }
}
