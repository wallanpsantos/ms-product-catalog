package com.example.catalog.domain.exception;

import com.example.catalog.domain.validation.handler.Notification;

/**
 * Exceção especial disparada quando o {@link com.example.catalog.domain.validation.handler.Notification}
 * contém erros acumulados.
 * <p>
 * Esta exceção carrega a lista completa de {@link com.example.catalog.domain.validation.Error},
 * permitindo que a camada de API (GlobalExceptionHandler) retorne uma resposta HTTP 422
 * detalhada com todos os problemas encontrados.
 * </p>
 * <p>
 * É o mecanismo de saída do Notification Pattern: acumula-se silenciosamente,
 * explode-se ruidosamente no final se houver problemas.
 * </p>
 */
public class NotificationException extends DomainException {

    public NotificationException(final String aMessage, final Notification notification) {
        super(aMessage, notification.getErrors());
    }

    public static NotificationException with(final String message, final Notification notification) {
        return new NotificationException(message, notification);
    }
}
