package com.example.catalog.infrastructure.adapter.output.gateway;

import com.example.catalog.UnitTest;
import com.example.catalog.domain.product.Product;
import com.example.catalog.domain.product.ProductID;
import com.example.catalog.infrastructure.adapter.output.persistence.ProductJpaEntity;
import com.example.catalog.infrastructure.adapter.output.persistence.ProductJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
class ProductJpaCommandAdapterTest {

    @Mock
    private ProductJpaRepository repository;

    @InjectMocks
    private ProductJpaCommandAdapter adapter;

    @Test
    @DisplayName("Deve criar um produto")
    void givenProduct_whenCreate_thenSaveAndReturn() {
        // Given
        final var product = Product.newProduct("Name", "Desc", "Cat", "Brand", BigDecimal.TEN, true);
        final var entity = ProductJpaEntity.from(product);
        when(repository.save(any(ProductJpaEntity.class))).thenReturn(entity);

        // When
        final var saved = adapter.create(product);

        // Then
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isEqualTo(product.getId());
        verify(repository).save(any(ProductJpaEntity.class));
    }

    @Test
    @DisplayName("Deve criar multiplos produtos")
    void givenProducts_whenCreateAll_thenSaveAndReturn() {
        // Given
        final var product = Product.newProduct("Name", "Desc", "Cat", "Brand", BigDecimal.TEN, true);
        final var entity = ProductJpaEntity.from(product);
        when(repository.saveAll(anyList())).thenReturn(List.of(entity));

        // When
        final var saved = adapter.createAll(List.of(product));

        // Then
        assertThat(saved).hasSize(1);
        verify(repository).saveAll(anyList());
    }

    @Test
    @DisplayName("Deve atualizar um produto")
    void givenProduct_whenUpdate_thenSaveAndReturn() {
        // Given
        final var product = Product.newProduct("Name", "Desc", "Cat", "Brand", BigDecimal.TEN, true);
        final var entity = ProductJpaEntity.from(product);
        when(repository.save(any(ProductJpaEntity.class))).thenReturn(entity);

        // When
        final var saved = adapter.update(product);

        // Then
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isEqualTo(product.getId());
        verify(repository).save(any(ProductJpaEntity.class));
    }

    @Test
    @DisplayName("Deve atualizar multiplos produtos")
    void givenProducts_whenUpdateAll_thenSaveAndReturn() {
        // Given
        final var product = Product.newProduct("Name", "Desc", "Cat", "Brand", BigDecimal.TEN, true);
        final var entity = ProductJpaEntity.from(product);
        when(repository.saveAll(anyList())).thenReturn(List.of(entity));

        // When
        final var saved = adapter.updateAll(List.of(product));

        // Then
        assertThat(saved).hasSize(1);
        verify(repository).saveAll(anyList());
    }

    @Test
    @DisplayName("Deve buscar produto por ID")
    void givenId_whenFindById_thenReturnOptional() {
        // Given
        final var id = ProductID.unique();
        final var entity = new ProductJpaEntity(id.getValue(), "N", "D", "C", "B", BigDecimal.TEN, true, Instant.now(), Instant.now());
        when(repository.findById(id.getValue())).thenReturn(Optional.of(entity));

        // When
        final var result = adapter.findById(id);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(id);
        verify(repository).findById(id.getValue());
    }

    @Test
    @DisplayName("Deve buscar todos produtos por IDs")
    void givenIds_whenFindAllById_thenReturnList() {
        // Given
        final var id = ProductID.unique();
        final var entity = new ProductJpaEntity(id.getValue(), "N", "D", "C", "B", BigDecimal.TEN, true, Instant.now(), Instant.now());
        when(repository.findAllById(anyList())).thenReturn(List.of(entity));

        // When
        final var result = adapter.findAllById(List.of(id));

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(id);
        verify(repository).findAllById(List.of(id.getValue()));
    }
}