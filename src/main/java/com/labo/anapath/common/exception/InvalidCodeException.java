package com.labo.anapath.common.exception;

/**
 * Exception levée lorsqu'un code de vérification est invalide ou expiré.
 * <p>
 * Utilisée principalement lors de la vérification d'un code TOTP 2FA (Google Authenticator).
 * Le {@link GlobalExceptionHandler} la traduit en réponse HTTP {@code 400 Bad Request}.
 * </p>
 */
public class InvalidCodeException extends RuntimeException {

    /**
     * Construit une {@link InvalidCodeException} avec un message descriptif.
     *
     * @param message description du problème lié au code fourni
     */
    public InvalidCodeException(String message) {
        super(message);
    }
}
