package com.example.catalog;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação para Testes Unitários.
 * <p>
 * Características:
 * - Testes Rápidos.
 * - Sem Contexto Spring (@SpringBootTest NÃO é carregado).
 * - Ideal para testar regras de negócio da camada de Domínio e Casos de Uso (Application).
 * - Dependências devem ser "mockadas" (ex: usando Mockito).
 * </p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@ExtendWith(MockitoExtension.class)
@Tag("unitTest")
public @interface UnitTest {
}
