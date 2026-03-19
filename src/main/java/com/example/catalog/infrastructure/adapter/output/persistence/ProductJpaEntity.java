package com.example.catalog.infrastructure.adapter.output.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Modelo de persistência mapeado para a tabela "products" do PostgreSQL.
 * <p>
 * Esta classe isola o domínio dos detalhes de infraestrutura (anotações do Spring Data JPA).
 * Inclui métodos de conversão `from` e `toEntity` para transitar entre os mundos.
 * </p>
 */
@Entity
@Table(name = "products")
@EntityListeners(AuditingEntityListener.class)
public class ProductJpaEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 4000)
    private String description;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Boolean active;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public ProductJpaEntity() {
    }

    @SuppressWarnings("java:S107")
    public ProductJpaEntity(String id, String name, String description, String category, String brand, BigDecimal price, Boolean active, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.brand = brand;
        this.price = price;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ProductJpaEntity from(final com.example.catalog.domain.product.Product product) {
        return new ProductJpaEntity(
                product.getId().getValue(),
                product.getName(),
                product.getDescription(),
                product.getCategory(),
                product.getBrand(),
                product.getPrice(),
                product.isActive(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    public com.example.catalog.domain.product.Product toEntity() {
        return com.example.catalog.domain.product.Product.with(
                com.example.catalog.domain.product.ProductID.from(this.id),
                this.name,
                this.description,
                this.category,
                this.brand,
                this.price,
                this.active,
                this.createdAt,
                this.updatedAt
        );
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductJpaEntity that = (ProductJpaEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ProductJpaEntity{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", brand='" + brand + '\'' +
                ", price=" + price +
                ", active=" + active +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}