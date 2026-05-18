package com.labo.anapath.common.exception;

/**
 * Exception levée lorsqu'une API externe est indisponible ou retourne une erreur inattendue.
 * Traduite en HTTP 503 Service Unavailable par le {@link GlobalExceptionHandler}.
 */
public class ExternalApiException extends RuntimeException {

    public ExternalApiException(String message) {
        super(message);
    }

    public ExternalApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
