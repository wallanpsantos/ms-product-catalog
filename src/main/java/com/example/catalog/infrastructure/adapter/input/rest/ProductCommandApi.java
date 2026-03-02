package com.example.catalog.infrastructure.adapter.input.rest;

import com.example.catalog.infrastructure.adapter.input.rest.dto.request.ProductRequest;
import com.example.catalog.infrastructure.adapter.input.rest.dto.request.UpdateProductRequest;
import com.example.catalog.infrastructure.adapter.input.rest.dto.response.CreateProductResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

/**
 * Interface que define o contrato da API REST focado exclusivamente em **Mutação (Commands)** para Produtos.
 * <p>
 * Utiliza anotações do Spring MVC para roteamento e do Swagger (OpenAPI 3) para documentação.
 * Separar a definição da API (contrato) da sua implementação (Controller) facilita a leitura,
 * manutenção e evita poluição visual na classe principal.
 * </p>
 * <p>
 * <strong>Por que este padrão?</strong><br>
 * Representa a interface do lado "Command" na arquitetura CQRS. Trabalhando junto com a {@link ProductQueryApi},
 * aplica o princípio CQS ao expor estritamente endpoints que alteram o estado do sistema (POST, PUT, DELETE),
 * sem retornar projeções completas de domínio.
 * </p>
 */
@RequestMapping(value = "/api/v1/products")
@Tag(name = "Products Commands", description = "Product Management API - Write Operations")
public interface ProductCommandApi {

    @PostMapping
    @Operation(summary = "Create a new product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created successfully"),
            @ApiResponse(responseCode = "422", description = "A validation error was thrown"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown"),
    })
    ResponseEntity<CreateProductResponse> createProduct(@RequestBody @Valid ProductRequest input);

    @PostMapping(value = "/batch")
    @Operation(summary = "Create multiple products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created successfully"),
            @ApiResponse(responseCode = "422", description = "A validation error was thrown"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown"),
    })
    ResponseEntity<List<CreateProductResponse>> createProductBatch(@RequestBody @Valid List<ProductRequest> input);

    @PutMapping(value = "{id}")
    @Operation(summary = "Update a product by it's identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully"),
            @ApiResponse(responseCode = "404", description = "Product was not found"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown"),
    })
    ResponseEntity<CreateProductResponse> updateProduct(@PathVariable(name = "id") String id, @RequestBody @Valid ProductRequest input);

    @PutMapping(value = "/batch")
    @Operation(summary = "Update multiple products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products updated successfully"),
            @ApiResponse(responseCode = "404", description = "One or more products were not found"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown"),
    })
    ResponseEntity<List<CreateProductResponse>> updateProductBatch(@RequestBody @Valid List<UpdateProductRequest> input);

    @DeleteMapping(value = "{id}")
    @Operation(summary = "Delete a product by it's identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Product was not found"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown"),
    })
    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    void deleteById(@PathVariable(name = "id") String id);
}
