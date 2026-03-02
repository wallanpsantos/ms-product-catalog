package com.example.catalog.domain.validation;

/**
 * Represents a single validation error in the domain.
 * Used by {@link ValidationHandler} to accumulate errors (Notification Pattern).
 */
public record Error(String message) {
}
