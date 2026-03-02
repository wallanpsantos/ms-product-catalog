package com.example.catalog.domain;

import com.example.catalog.domain.exception.DomainException;
import com.example.catalog.domain.validation.Error;

import java.math.BigDecimal;

/**
 * Fornece utilitários de asserção fluente para garantir invariantes de domínio.
 * Projetado para uso dentro de construtores de entidades e métodos de fábrica.
 * <p>
 * Esta classe atua como uma **guarda de falha rápida (fail-fast)**: lança uma {@link com.example.catalog.domain.exception.DomainException}
 * imediatamente na primeira violação, diferente do {@link com.example.catalog.domain.validation.ValidationHandler}
 * baseado no Notification Pattern que acumula erros.
 * </p>
 * <p>
 * O objetivo é impedir que objetos de domínio sejam instanciados em um estado tecnicamente inválido
 * (ex: nulo, vazio, negativo) que quebraria a lógica interna do sistema.
 * </p>
 */
public abstract class AssertionConcern {

    protected void assertArgumentNotNull(Object value, String message) {
        if (value == null) {
            fail(message);
        }
    }

    protected void assertArgumentNotEmpty(Object value, String message) {
        if (value == null || (value instanceof String stringValue && (stringValue.isBlank() || stringValue.trim().isEmpty()))) {
            fail(message);
        }
    }

    protected void assertArgumentLength(String value, int maxLength, String message) {
        if (value != null && value.trim().length() > maxLength) {
            fail(message);
        }
    }

    protected void assertArgumentPositive(BigDecimal value, String message) {
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            fail(message);
        }
    }

    protected void assertArgumentPositive(Number value, String message) {
        if (value == null || isNonFinite(value) || value.doubleValue() <= 0.0d) {
            fail(message);
        }
    }

    protected void assertArgumentPositive(int value, String message) {
        if (value <= 0) {
            fail(message);
        }
    }

    protected void assertArgumentPositive(long value, String message) {
        if (value <= 0L) {
            fail(message);
        }
    }

    protected void assertArgumentPositive(double value, String message) {
        if (value <= 0.0d) {
            fail(message);
        }
    }

    private static boolean isNonFinite(Number value) {
        if (value instanceof Double d) {
            return Double.isNaN(d) || Double.isInfinite(d);
        }
        if (value instanceof Float f) {
            return Float.isNaN(f) || Float.isInfinite(f);
        }
        return false;
    }

    private static void fail(String message) {
        throw DomainException.with(new Error(message));
    }
}