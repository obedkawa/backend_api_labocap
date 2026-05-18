package com.labo.anapath.common.exception;

/**
 * Exception levée lorsqu'une opération demandée est invalide dans le contexte courant.
 * <p>
 * Différente de {@link BusinessException}, elle signale une opération structurellement
 * incorrecte (ex. tentative de modifier un enregistrement clôturé, appel dans un mauvais état).
 * Le {@link GlobalExceptionHandler} la traduit en réponse HTTP {@code 400 Bad Request}.
 * </p>
 */
public class InvalidOperationException extends RuntimeException {

    /**
     * Construit une {@link InvalidOperationException} avec un message descriptif.
     *
     * @param message description de l'opération invalide
     */
    public InvalidOperationException(String message) {
        super(message);
    }
}
