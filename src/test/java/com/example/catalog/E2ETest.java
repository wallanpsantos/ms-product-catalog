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
 * Anotação para Testes Ponta a Ponta (End-to-End / E2E).
 * <p>
 * Características:
 * - Sobe a aplicação completa com o servidor web em uma porta aleatória (RANDOM_PORT).
 * - Sobe banco de dados real via Testcontainers.
 * - Limpa o banco automaticamente após cada teste.
 * - Ideal para testes de caixa preta chamando as APIs reais (ex: usando REST Assured)
 * e validando o fluxo completo desde o Controller até o Banco de Dados.
 * </p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(DatabaseCleanUpExtension.class)
@Import(TestcontainersConfig.class)
@Tag("e2eTest")
public @interface E2ETest {
}
