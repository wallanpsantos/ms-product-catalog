package com.example.catalog.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Configuração do MongoDB.
 * <p>
 * Habilita a Auditoria (`@EnableMongoAuditing`) para que as anotações
 * {@link org.springframework.data.annotation.CreatedDate} e {@link org.springframework.data.annotation.LastModifiedDate}
 * sejam preenchidas automaticamente pelo Spring Data.
 * </p>
 */
@Configuration
@EnableMongoAuditing
public class MongoConfig {

    @Bean
    public MongoTemplate mongoTemplate(final MongoDatabaseFactory factory) {
        return new MongoTemplate(factory);
    }
}
