package com.example.catalog.infrastructure.adapter.input.rest;

import com.example.catalog.E2ETest;
import com.example.catalog.infrastructure.adapter.input.rest.dto.request.ProductRequest;
import com.example.catalog.infrastructure.adapter.output.persistence.ProductJpaEntity;
import com.example.catalog.infrastructure.adapter.output.persistence.ProductJpaRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
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
    @DisplayName("Deve buscar produto existente e retornar 200")
    void shouldReturn200WhenGettingExistingProduct() {
        // Given
        String id = UUID.randomUUID().toString();
        repository.save(new ProductJpaEntity(
                id,
                "Monitor",
                "Monitor 4k",
                "Acessorios",
                "LG",
                new BigDecimal("1500.00"),
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        ));

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
        repository.save(new ProductJpaEntity(
                id,
                "Cadeira",
                "Cadeira Ergonômica",
                "Moveis",
                "Herman Miller",
                new BigDecimal("5000.00"),
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        ));

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
}