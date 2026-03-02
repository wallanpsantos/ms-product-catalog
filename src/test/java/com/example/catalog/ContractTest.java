package com.example.catalog;

import com.example.catalog.infrastructure.config.DatabaseCleanUpExtension;
import com.example.catalog.infrastructure.config.TestcontainersConfig;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação para Testes de Contrato e APIs Externas.
 * <p>
 * Características:
 * - Sobe o contexto do Spring Boot (@SpringBootTest).
 * - Configura o WireMock via StubRunner para simular respostas de APIs externas.
 * - Ideal para testar clientes HTTP (ex: Feign, RestClient, RestTemplate) e garantir
 * que a comunicação com serviços terceiros está aderente ao contrato.
 * </p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@SpringBootTest
@Import(TestcontainersConfig.class)
@ExtendWith(DatabaseCleanUpExtension.class)
@AutoConfigureStubRunner(stubsMode = StubRunnerProperties.StubsMode.LOCAL)
@Tag("contractTest")
public @interface ContractTest {
}
