package com.example.catalog.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Configuração do JPA.
 * <p>
 * Habilita a Auditoria (`@EnableJpaAuditing`) para que as anotações
 * `@CreatedDate` e `@LastModifiedDate` funcionem nas entidades.
 * </p>
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}
