package com.example.catalog.domain.validation.handler;

import com.example.catalog.UnitTest;
import com.example.catalog.domain.exception.DomainException;
import com.example.catalog.domain.validation.Error;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
class NotificationTest {

    @Test
    @DisplayName("Deve criar uma Notification vazia")
    void givenEmpty_whenCreate_thenShouldReturnEmptyNotification() {
        // When
        final var notification = Notification.create();

        // Then
        assertThat(notification)
                .returns(false, Notification::hasError)
                .returns(true, n -> n.getErrors().isEmpty());
    }

    @Test
    @DisplayName("Deve criar Notification a partir de uma Exception")
    void givenException_whenCreate_thenShouldReturnNotificationWithError() {
        // Given
        final var exception = new RuntimeException("Error message");

        // When
        final var notification = Notification.create(exception);

        // Then
        assertThat(notification.hasError()).isTrue();
        assertThat(notification.firstError().get().message()).isEqualTo("Error message");
    }

    @Test
    @DisplayName("Deve criar Notification a partir de um Error")
    void givenError_whenCreate_thenShouldReturnNotificationWithError() {
        // Given
        final var error = new Error("Validation error");

        // When
        final var notification = Notification.create(error);

        // Then
        assertThat(notification.hasError()).isTrue();
        assertThat(notification.firstError().get().message()).isEqualTo("Validation error");
    }

    @Test
    @DisplayName("Deve anexar Errors a uma Notification")
    void givenErrors_whenAppend_thenShouldAddErrors() {
        // Given
        final var notification = Notification.create();
        final var error1 = new Error("Error 1");
        final var error2 = new Error("Error 2");

        // When
        notification.append(error1);
        notification.append(error2);

        // Then
        assertThat(notification.hasError()).isTrue();
        assertThat(notification.getErrors()).hasSize(2);
        assertThat(notification.getErrors()).containsExactly(error1, error2);
    }

    @Test
    @DisplayName("Deve anexar Handler a uma Notification")
    void givenHandler_whenAppend_thenShouldAddErrors() {
        // Given
        final var notification = Notification.create();
        final var handler = Notification.create(new Error("Error from handler"));

        // When
        notification.append(handler);

        // Then
        assertThat(notification.hasError()).isTrue();
        assertThat(notification.getErrors()).hasSize(1);
        assertThat(notification.firstError().get().message()).isEqualTo("Error from handler");
    }

    @Test
    @DisplayName("Deve validar execucao com sucesso")
    void givenValidation_whenValidate_thenShouldReturnOptional() {
        // Given
        final var notification = Notification.create();

        // When
        final var result = notification.validate(() -> "Success");

        // Then
        assertThat(notification.hasError()).isFalse();
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo("Success");
    }

    @Test
    @DisplayName("Deve validar capturando DomainException")
    void givenValidationThrowingDomainException_whenValidate_thenShouldAccumulateError() {
        // Given
        final var notification = Notification.create();
        final var domainException = DomainException.with(new Error("Domain error"));

        // When
        final var result = notification.validate(() -> {
            throw domainException;
        });

        // Then
        assertThat(result).isEmpty();
        assertThat(notification.hasError()).isTrue();
        assertThat(notification.getErrors()).hasSize(1);
        assertThat(notification.firstError().get().message()).isEqualTo("Domain error");
    }

    @Test
    @DisplayName("Deve validar capturando Exception generica")
    void givenValidationThrowingException_whenValidate_thenShouldAccumulateError() {
        // Given
        final var notification = Notification.create();

        // When
        final var result = notification.validate(() -> {
            throw new RuntimeException("Generic error");
        });

        // Then
        assertThat(result).isEmpty();
        assertThat(notification.hasError()).isTrue();
        assertThat(notification.getErrors()).hasSize(1);
        assertThat(notification.firstError().get().message()).isEqualTo("Generic error");
    }
}