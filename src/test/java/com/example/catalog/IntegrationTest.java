package com.example.catalog;

import com.example.catalog.infrastructure.config.DatabaseCleanUpExtension;
import com.example.catalog.infrastructure.config.TestcontainersConfig;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação para Testes de Integração.
 * <p>
 * Características:
 * - Sobe o contexto do Spring Boot (@SpringBootTest).
 * - Utiliza banco de dados real via Testcontainers (ex: MongoDB).
 * - Realiza limpeza automática do banco após cada teste.
 * - Ideal para testar Adaptadores de Saída (Repositórios/Gateways) e queries complexas.
 * - Não expõe o servidor web numa porta real.
 * </p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@SpringBootTest
@Import(TestcontainersConfig.class)
@ExtendWith(DatabaseCleanUpExtension.class)
@Tag("integrationTest")
public @interface IntegrationTest {
}
