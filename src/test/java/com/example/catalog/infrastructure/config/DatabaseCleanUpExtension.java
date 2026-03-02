package com.example.catalog.infrastructure.config;

import com.example.catalog.infrastructure.adapter.output.persistence.ProductMongoRepository;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collection;
import java.util.List;

/**
 * Extensão do JUnit 5 responsável por limpar o banco de dados antes de cada teste.
 * <p>
 * O objetivo é garantir um ambiente limpo e isolado (Clean Slate) para cada cenário de teste,
 * deletando todos os dados de repositórios específicos (ex: ProductMongoRepository).
 * </p>
 * <p>
 * Usada principalmente pelas anotações customizadas como @IntegrationTest e @E2ETest.
 * </p>
 */
public class DatabaseCleanUpExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(final ExtensionContext context) throws Exception {
        final var appContext = SpringExtension.getApplicationContext(context);

        cleanUp(List.of(
                appContext.getBean(ProductMongoRepository.class)
        ));
    }

    @SuppressWarnings("rawtypes")
    private void cleanUp(final Collection<CrudRepository> repositories) {
        repositories.forEach(CrudRepository::deleteAll);
    }
}
