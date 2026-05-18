package com.labo.anapath.common.exception;

/**
 * Exception levée lorsqu'une règle métier est violée.
 * <p>
 * Utilisée pour des violations de règles propres au domaine (ex. état invalide d'un dossier,
 * quota dépassé, transition interdite). Le {@link GlobalExceptionHandler} la traduit
 * en réponse HTTP {@code 422 Unprocessable Entity}.
 * </p>
 */
public class BusinessException extends RuntimeException {

    /**
     * Construit une {@link BusinessException} avec un message descriptif.
     *
     * @param message description de la violation métier
     */
    public BusinessException(String message) {
        super(message);
    }

    /**
     * Construit une {@link BusinessException} avec un message et la cause technique.
     *
     * @param message description de la violation métier
     * @param cause   exception technique sous-jacente
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
