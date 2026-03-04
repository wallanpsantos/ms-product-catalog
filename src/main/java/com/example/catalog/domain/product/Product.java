package com.example.catalog.domain.product;

import com.example.catalog.domain.Entity;
import com.example.catalog.domain.validation.ValidationHandler;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Raiz do Agregado de Produto (Aggregate Root).
 * <p>
 * Esta classe representa o conceito central de um Produto no catálogo. Como Raiz de Agregado,
 * ela é responsável por manter a consistência de todos os seus atributos e garantir que
 * suas invariantes de negócio sejam respeitadas.
 * </p>
 * <p>
 * <strong>Design Rico vs. Anêmico:</strong>
 * Esta classe não é apenas um contêiner de dados (DTO). Ela possui métodos de negócio
 * ({@code update}, {@code deactivate}) e validação, encapsulando o comportamento do domínio.
 * </p>
 * <p>
 * <strong>CQS (Command Query Separation):</strong>
 * Segue rigorosamente o princípio CQS.
 * <ul>
 *   <li><strong>Commands (Comandos):</strong> Métodos como {@code update} e {@code deactivate} alteram o estado
 *       mas retornam {@code void} (ou a própria instância para fluência, sem retornar dados de consulta).</li>
 *   <li><strong>Queries (Consultas):</strong> Métodos getters retornam dados mas nunca alteram o estado.</li>
 * </ul>
 * </p>
 * <p>
 * <strong>Validação:</strong>
 * Utiliza {@link com.example.catalog.domain.AssertionConcern} no construtor para validação defensiva (fail-fast)
 * de integridade técnica, e {@link ProductValidator} (Notification Pattern) para
 * validação de regras de negócio complexas.
 * </p>
 * <p>
 * <strong>Por que NÃO usamos {@code record} para Agregados?</strong>
 * <ol>
 *   <li><strong>Mutabilidade e Ciclo de Vida:</strong> Agregados mudam de estado ao longo do tempo (ex: alterar preço, desativar produto).
 *   Records em Java são estritamente imutáveis. Usá-los forçaria a recriação do agregado a cada alteração, o que quebra a clareza e a gerência de estado do modelo de domínio.</li>
 *   <li><strong>Identidade:</strong> Assim como Entidades, Agregados são identificados exclusivamente pelo seu ID.
 *   Records implementam igualdade estrutural comparando todos os campos, o que viola o princípio de identidade contínua do DDD.</li>
 * </ol>
 * </p>
 */
public class Product extends Entity<ProductID> {

    private String name;
    private String description;
    private String category;
    private String brand;
    private BigDecimal price;
    private boolean active;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @SuppressWarnings("java:S107") // Reconstitution requires all fields
    private Product(
            final ProductID id,
            final String name,
            final String description,
            final String category,
            final String brand,
            final BigDecimal price,
            final boolean active,
            final LocalDateTime createdAt,
            final LocalDateTime updatedAt
    ) {
        super(id);
        this.assertArgumentNotEmpty(name, "'name' should not be empty or null");
        this.assertArgumentLength(name, 255, "'name' must be between 1 and 255 characters");

        this.assertArgumentNotEmpty(description, "'description' should not be empty or null");
        this.assertArgumentLength(description, 4000, "'description' must be at most 4000 characters");

        this.assertArgumentNotEmpty(category, "'category' should not be empty or null");

        this.assertArgumentNotEmpty(brand, "'brand' should not be empty or null");

        this.assertArgumentPositive(price, "'price' must be greater than zero");

        this.name = name;
        this.description = description;
        this.category = category;
        this.brand = brand;
        this.price = price;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Product newProduct(
            final String name,
            final String description,
            final String category,
            final String brand,
            final BigDecimal price,
            final boolean active
    ) {
        final var now = LocalDateTime.now();
        return new Product(ProductID.unique(), name, description, category, brand, price, active, now, now);
    }

    @SuppressWarnings("java:S107") // Reconstitution requires all fields
    public static Product with(
            final ProductID id,
            final String name,
            final String description,
            final String category,
            final String brand,
            final BigDecimal price,
            final boolean active,
            final LocalDateTime createdAt,
            final LocalDateTime updatedAt
    ) {
        return new Product(id, name, description, category, brand, price, active, createdAt, updatedAt);
    }

    public void update(
            final String name,
            final String description,
            final String category,
            final String brand,
            final BigDecimal price,
            final boolean active
    ) {
        this.assertArgumentNotEmpty(name, "'name' should not be empty or null");
        this.assertArgumentLength(name, 255, "'name' must be between 1 and 255 characters");
        this.assertArgumentNotEmpty(description, "'description' should not be empty or null");
        this.assertArgumentLength(description, 4000, "'description' must be at most 4000 characters");
        this.assertArgumentNotEmpty(category, "'category' should not be empty or null");
        this.assertArgumentNotEmpty(brand, "'brand' should not be empty or null");
        this.assertArgumentPositive(price, "'price' must be greater than zero");

        this.name = name;
        this.description = description;
        this.category = category;
        this.brand = brand;
        this.price = price;
        this.active = active;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public void validate(final ValidationHandler handler) {
        new ProductValidator(this, handler).validate();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getBrand() {
        return brand;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public boolean isActive() {
        return active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Product product)) return false;
        if (!super.equals(o)) return false;
        return active == product.active && Objects.equals(name, product.name) && Objects.equals(description, product.description) && Objects.equals(category, product.category) && Objects.equals(brand, product.brand) && Objects.equals(price, product.price) && Objects.equals(createdAt, product.createdAt) && Objects.equals(updatedAt, product.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, description, category, brand, price, active, createdAt, updatedAt);
    }
}
