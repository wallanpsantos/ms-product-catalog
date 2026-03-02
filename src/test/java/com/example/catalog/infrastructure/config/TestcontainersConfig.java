package com.example.catalog.infrastructure.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MongoDBContainer;

/**
 * Configuração central para a inicialização e gerenciamento de dependências externas
 * usando Testcontainers durante a execução dos testes de integração.
 * <p>
 * O container do MongoDB é levantado uma única vez e reaproveitado para acelerar o tempo de execução.
 * A anotação {@link ServiceConnection} registra automaticamente as propriedades de conexão
 * (ex: spring.data.mongodb.uri) no contexto do Spring.
 * </p>
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfig {

    @Bean
    @ServiceConnection
    // Suprime o alerta de recurso não fechado (AutoCloseable). O ciclo de vida do container 
    // é gerenciado automaticamente pelo Spring Boot, que se encarregará do encerramento.
    @SuppressWarnings("resource")
    MongoDBContainer mongoDBContainer() {
        return new MongoDBContainer("mongo:8.0.11")
                .withReuse(true);
    }
}