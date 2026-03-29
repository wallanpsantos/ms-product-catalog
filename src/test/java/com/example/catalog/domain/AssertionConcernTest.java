package com.example.catalog.domain;

import com.example.catalog.UnitTest;
import com.example.catalog.domain.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@UnitTest
class AssertionConcernTest {

    private final DummyAssertionConcern assertion = new DummyAssertionConcern();

    @Test
    @DisplayName("Deve passar quando objeto nao for nulo")
    void givenNonNullObject_whenAssertNotNull_thenPass() {
        assertThatCode(() -> assertion.testNotNull(new Object(), "Error"))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Deve falhar quando objeto for nulo")
    void givenNullObject_whenAssertNotNull_thenThrowDomainException() {
        assertThatThrownBy(() -> assertion.testNotNull(null, "Cannot be null"))
                .isInstanceOf(DomainException.class)
                .hasMessage("Cannot be null");
    }

    @Test
    @DisplayName("Deve falhar quando string for vazia")
    void givenEmptyString_whenAssertNotEmpty_thenThrowDomainException() {
        assertThatThrownBy(() -> assertion.testNotEmpty("   ", "Cannot be empty"))
                .isInstanceOf(DomainException.class)
                .hasMessage("Cannot be empty");
    }

    @Test
    @DisplayName("Deve passar quando string tiver tamanho valido")
    void givenValidStringLength_whenAssertLength_thenPass() {
        assertThatCode(() -> assertion.testLength("123", 5, "Error"))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Deve falhar quando string tiver tamanho maior que maximo")
    void givenInvalidStringLength_whenAssertLength_thenThrowDomainException() {
        assertThatThrownBy(() -> assertion.testLength("123456", 5, "Max length is 5"))
                .isInstanceOf(DomainException.class)
                .hasMessage("Max length is 5");
    }

    @Test
    @DisplayName("Deve passar quando BigDecimal for positivo")
    void givenPositiveBigDecimal_whenAssertPositive_thenPass() {
        assertThatCode(() -> assertion.testPositive(new BigDecimal("10.0"), "Error"))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Deve falhar quando BigDecimal for zero ou negativo")
    void givenZeroOrNegativeBigDecimal_whenAssertPositive_thenThrowDomainException() {
        assertThatThrownBy(() -> assertion.testPositive(BigDecimal.ZERO, "Must be positive"))
                .isInstanceOf(DomainException.class)
                .hasMessage("Must be positive");
    }

    @Test
    @DisplayName("Deve falhar quando Double for negativo, infinito ou NaN")
    void givenInvalidDouble_whenAssertPositive_thenThrowDomainException() {
        assertThatThrownBy(() -> assertion.testPositiveNumber(Double.NaN, "Invalid Number"))
                .isInstanceOf(DomainException.class)
                .hasMessage("Invalid Number");

        assertThatThrownBy(() -> assertion.testPositiveNumber(-1.0d, "Must be positive"))
                .isInstanceOf(DomainException.class)
                .hasMessage("Must be positive");

        assertThatThrownBy(() -> assertion.testPositiveNumber(Float.NaN, "Invalid Number"))
                .isInstanceOf(DomainException.class)
                .hasMessage("Invalid Number");
    }

    @Test
    @DisplayName("Deve falhar quando inteiros e longs e doubles forem invalidos")
    void givenInvalidPrimitives_whenAssertPositive_thenThrowDomainException() {
        assertThatThrownBy(() -> assertion.testPositiveInt(0, "Invalid Integer"))
                .isInstanceOf(DomainException.class)
                .hasMessage("Invalid Integer");

        assertThatThrownBy(() -> assertion.testPositiveLong(0L, "Invalid Long"))
                .isInstanceOf(DomainException.class)
                .hasMessage("Invalid Long");

        assertThatThrownBy(() -> assertion.testPositiveDouble(0.0d, "Invalid Double"))
                .isInstanceOf(DomainException.class)
                .hasMessage("Invalid Double");
    }

    private static class DummyAssertionConcern extends AssertionConcern {
        public void testNotNull(Object value, String message) {
            this.assertArgumentNotNull(value, message);
        }

        public void testNotEmpty(Object value, String message) {
            this.assertArgumentNotEmpty(value, message);
        }

        public void testLength(String value, int max, String message) {
            this.assertArgumentLength(value, max, message);
        }

        public void testPositive(BigDecimal value, String message) {
            this.assertArgumentPositive(value, message);
        }

        public void testPositiveNumber(Number value, String message) {
            this.assertArgumentPositive(value, message);
        }

        public void testPositiveInt(int value, String message) {
            this.assertArgumentPositive(value, message);
        }

        public void testPositiveLong(long value, String message) {
            this.assertArgumentPositive(value, message);
        }

        public void testPositiveDouble(double value, String message) {
            this.assertArgumentPositive(value, message);
        }
    }
}