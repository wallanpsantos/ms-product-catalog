package com.example.catalog.infrastructure.adapter.input.exception;

import com.example.catalog.domain.exception.DomainException;
import com.example.catalog.domain.exception.NotFoundException;
import com.example.catalog.domain.validation.Error;
import com.example.catalog.infrastructure.adapter.input.rest.dto.response.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

/**
 * Interceptador global de exceções da API.
 * <p>
 * Centraliza o tratamento de erros, convertendo exceções de domínio e de validação
 * em respostas HTTP padronizadas (RFC 7807 ou formato customizado).
 * </p>
 * <p>
 * Mapeamentos principais:
 * <ul>
 *   <li>{@link NotFoundException} -> HTTP 404</li>
 *   <li>{@link DomainException} -> HTTP 422 (Unprocessable Entity)</li>
 *   <li>{@link MethodArgumentNotValidException} (Bean Validation) -> HTTP 422</li>
 *   <li>Outras Exceções -> HTTP 500</li>
 * </ul>
 * </p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            final MethodArgumentNotValidException ex,
            final HttpHeaders headers,
            final HttpStatusCode status,
            final WebRequest request
    ) {
        log.error("action=validationException fields={}", ex.getBindingResult().getFieldErrorCount());
        final var errors = ex.getBindingResult().getAllErrors().stream()
                .map(this::convertError)
                .toList();

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Validation Error", errors, LocalDateTime.now()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(final NotFoundException ex) {
        log.error("action=notFoundException message={}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(final DomainException ex) {
        log.error("action=domainException message={}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), ex.getMessage(), ex.getErrors(), LocalDateTime.now()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(final Exception ex) {
        log.error("action=unexpectedException message={}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "An unexpected error occurred", LocalDateTime.now()));
    }

    private Error convertError(final ObjectError error) {
        final var message = error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid value";
        if (error instanceof FieldError fieldError) {
            return new Error("Field '%s': %s".formatted(fieldError.getField(), message));
        }
        return new Error(message);
    }
}
