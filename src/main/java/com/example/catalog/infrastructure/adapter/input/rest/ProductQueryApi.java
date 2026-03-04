package com.example.catalog.infrastructure.adapter.input.rest;

import com.example.catalog.domain.pagination.Pagination;
import com.example.catalog.infrastructure.adapter.input.rest.dto.request.SearchRequest;
import com.example.catalog.infrastructure.adapter.input.rest.dto.response.ProductResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Interface que define o contrato da API REST focado exclusivamente em **Leitura (Queries)** para Produtos.
 * <p>
 * Utiliza anotações do Spring MVC para roteamento e do Swagger (OpenAPI 3) para documentação.
 * Separar a definição da API (contrato) da sua implementação (Controller) facilita a leitura,
 * manutenção e evita poluição visual na classe principal.
 * </p>
 * <p>
 * <strong>Por que este padrão?</strong><br>
 * Representa a interface do lado "Query" na arquitetura CQRS. Trabalhando junto com a {@link ProductCommandApi},
 * aplica o princípio CQS ao expor estritamente endpoints de consulta de dados (GET, POST /search),
 * garantindo a ausência de métodos que causem efeitos colaterais.
 * </p>
 */
@RequestMapping(value = "/api/v1/products")
@Tag(name = "Products Queries", description = "Product Management API - Read Operations")
public interface ProductQueryApi {

    @GetMapping(value = "{id}")
    @Operation(summary = "Get a product by it's identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Product was not found"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown"),
    })
    ResponseEntity<ProductResponse> getById(@PathVariable(name = "id") String id);

    @GetMapping
    @Operation(summary = "List all active products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listed successfully"),
            @ApiResponse(responseCode = "422", description = "A invalid parameter was received"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown"),
    })
    ResponseEntity<Pagination<ProductResponse>> listProducts(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "perPage", defaultValue = "10") int perPage,
            @RequestParam(name = "sort", defaultValue = "name") String sort,
            @RequestParam(name = "dir", defaultValue = "asc") String direction
    );

    @PostMapping(value = "/search")
    @Operation(summary = "Search products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search successfully"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown"),
    })
    ResponseEntity<List<ProductResponse>> searchProducts(@RequestBody @Valid SearchRequest input);
}
