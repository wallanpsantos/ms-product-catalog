package com.example.catalog.infrastructure.adapter.input.rest;

import com.example.catalog.E2ETest;
import com.example.catalog.infrastructure.adapter.input.rest.dto.request.ProductRequest;
import com.example.catalog.infrastructure.adapter.input.rest.dto.request.SearchRequest;
import com.example.catalog.infrastructure.adapter.input.rest.dto.request.UpdateProductRequest;
import com.example.catalog.infrastructure.adapter.output.persistence.ProductJpaEntityFixture;
import com.example.catalog.infrastructure.adapter.output.persistence.ProductJpaRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

@E2ETest
class ProductE2ETest {

    @LocalServerPort
    private int port;

    @Autowired
    private ProductJpaRepository repository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    @DisplayName("Deve criar um produto com sucesso e retornar 201 via API E2E")
    void shouldCreateProductSuccessfully() {
        // Given
        ProductRequest request = new ProductRequest("Notebook", "Notebook de alta performance", "Eletronicos", "Dell", new BigDecimal("5000.00"), true);

        // When
        String productId = given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v1/products")
                .then()
                .statusCode(201)
                .header("Location", org.hamcrest.Matchers.containsString("/api/v1/products/"))
                .body("id", notNullValue())
                .extract().path("id");

        // Then
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/v1/products/" + productId)
                .then()
                .statusCode(200)
                .body("name", equalTo("Notebook"))
                .body("description", equalTo("Notebook de alta performance"))
                .body("category", equalTo("Eletronicos"))
                .body("brand", equalTo("Dell"))
                // RestAssured handles float to double mapping for JSON numbers
                .body("price", equalTo(5000.00f))
                .body("active", equalTo(true));
    }

    @Test
    @DisplayName("Deve retornar 422 ao tentar criar um produto com dados inválidos")
    void shouldReturn422WhenCreatingInvalidProduct() {
        // Given
        ProductRequest request = new ProductRequest("", "Descricao", "Categoria", "Marca", new BigDecimal("-10.00"), null);

        // When & Then
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v1/products")
                .then()
                .statusCode(422);
    }

    @Test
    @DisplayName("Deve atualizar um produto com sucesso e retornar 200")
    void shouldUpdateProductSuccessfully() {
        // Given
        String id = UUID.randomUUID().toString();
        repository.save(ProductJpaEntityFixture.active(id, "Monitor"));

        ProductRequest request = new ProductRequest("Monitor Atualizado", "Monitor 8k", "Acessorios", "LG", new BigDecimal("2500.00"), false);

        // When
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put("/api/v1/products/" + id)
                .then()
                .statusCode(200);

        // Then
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/v1/products/" + id)
                .then()
                .statusCode(200)
                .body("name", equalTo("Monitor Atualizado"))
                .body("price", equalTo(2500.00f))
                .body("active", equalTo(false));
    }

    @Test
    @DisplayName("Deve retornar 404 ao tentar atualizar um produto que nao existe")
    void shouldReturn404WhenUpdatingNonExistentProduct() {
        // Given
        ProductRequest request = new ProductRequest("Monitor", "Monitor 4k", "Acessorios", "LG", new BigDecimal("1500.00"), true);

        // When & Then
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put("/api/v1/products/invalid-id")
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Deve buscar produto existente e retornar 200")
    void shouldReturn200WhenGettingExistingProduct() {
        // Given
        String id = UUID.randomUUID().toString();
        repository.save(ProductJpaEntityFixture.active(id, "Monitor"));

        // When & Then
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/v1/products/" + id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("name", equalTo("Monitor"));
    }

    @Test
    @DisplayName("Deve inativar produto com sucesso e retornar 204")
    void shouldDeactivateProductSuccessfully() {
        // Given
        String id = UUID.randomUUID().toString();
        repository.save(ProductJpaEntityFixture.active(id, "Cadeira"));

        // When & Then
        given()
                .contentType(ContentType.JSON)
                .when()
                .delete("/api/v1/products/" + id)
                .then()
                .statusCode(204);

        // Validating via GET
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/v1/products/" + id)
                .then()
                .statusCode(200)
                .body("active", equalTo(false));
    }

    @Test
    @DisplayName("Deve criar produtos em lote com sucesso e retornar 201")
    void shouldCreateProductBatchSuccessfully() {
        // Given
        ProductRequest request1 = new ProductRequest("P1", "D1", "C1", "B1", new BigDecimal("10.00"), true);
        ProductRequest request2 = new ProductRequest("P2", "D2", "C2", "B2", new BigDecimal("20.00"), true);

        // When
        given()
                .contentType(ContentType.JSON)
                .body(List.of(request1, request2))
                .when()
                .post("/api/v1/products/batch")
                .then()
                .statusCode(201)
                .body("$", hasSize(2));
    }

    @Test
    @DisplayName("Deve atualizar produtos em lote com sucesso e retornar 200")
    void shouldUpdateProductBatchSuccessfully() {
        // Given
        String id1 = UUID.randomUUID().toString();
        String id2 = UUID.randomUUID().toString();

        repository.save(ProductJpaEntityFixture.active(id1, "P1"));
        repository.save(ProductJpaEntityFixture.active(id2, "P2"));

        UpdateProductRequest req1 = new UpdateProductRequest(id1, "P1_UP", "D1", "C1", "B1", new BigDecimal("15.00"), true);
        UpdateProductRequest req2 = new UpdateProductRequest(id2, "P2_UP", "D2", "C2", "B2", new BigDecimal("25.00"), true);

        // When
        given()
                .contentType(ContentType.JSON)
                .body(List.of(req1, req2))
                .when()
                .put("/api/v1/products/batch")
                .then()
                .statusCode(200);

        // Then
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/v1/products/" + id1)
                .then()
                .statusCode(200)
                .body("name", equalTo("P1_UP"))
                .body("price", equalTo(15.00f));
    }

    @Test
    @DisplayName("Deve listar produtos ativos com paginacao com sucesso e retornar 200")
    void shouldListProductsWithPaginationSuccessfully() {
        // Given
        repository.deleteAll();
        repository.save(ProductJpaEntityFixture.active(UUID.randomUUID().toString(), "Teclado"));
        repository.save(ProductJpaEntityFixture.active(UUID.randomUUID().toString(), "Mouse"));
        repository.save(ProductJpaEntityFixture.inactive(UUID.randomUUID().toString()));

        // When & Then
        given()
                .contentType(ContentType.JSON)
                .queryParam("page", 0)
                .queryParam("perPage", 10)
                .queryParam("sort", "name")
                .queryParam("dir", "asc")
                .when()
                .get("/api/v1/products")
                .then()
                .statusCode(200)
                .body("currentPage", equalTo(0))
                .body("perPage", equalTo(10))
                .body("total", equalTo(2)) // Only 2 active products
                .body("items", hasSize(2));
    }

    @Test
    @DisplayName("Deve buscar produtos por termo (query) com sucesso e retornar 200")
    void shouldSearchProductsSuccessfully() {
        // Given
        repository.deleteAll();
        repository.save(ProductJpaEntityFixture.active(UUID.randomUUID().toString(), "Teclado Redragon"));
        repository.save(ProductJpaEntityFixture.active(UUID.randomUUID().toString(), "Mouse Razer"));

        SearchRequest request = new SearchRequest("Redragon");

        // When & Then
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v1/products/search")
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].name", equalTo("Teclado Redragon"));
    }

    @Test
    @DisplayName("Deve retornar 422 ao buscar produtos com query vazia")
    void shouldReturn422WhenSearchingWithBlankQuery() {
        // Given
        SearchRequest request = new SearchRequest("");

        // When & Then
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v1/products/search")
                .then()
                .statusCode(422);
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar produto inexistente")
    void shouldReturn404WhenGettingNonExistentProduct() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/v1/products/" + UUID.randomUUID())
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Deve retornar 404 ao deletar produto inexistente")
    void shouldReturn404WhenDeletingNonExistentProduct() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .delete("/api/v1/products/" + UUID.randomUUID())
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Deve retornar 404 no batch update quando algum ID não existe")
    void shouldReturn404WhenBatchUpdateHasMissingId() {
        UpdateProductRequest req = new UpdateProductRequest(UUID.randomUUID().toString(), "P", "D", "C", "B", BigDecimal.TEN, true);
        given()
                .contentType(ContentType.JSON)
                .body(List.of(req))
                .when()
                .put("/api/v1/products/batch")
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando nenhum produto corresponde à busca")
    void shouldReturnEmptyListWhenSearchHasNoMatches() {
        repository.deleteAll();
        SearchRequest request = new SearchRequest("NaoExiste");
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v1/products/search")
                .then()
                .statusCode(200)
                .body("$", hasSize(0));
    }
}