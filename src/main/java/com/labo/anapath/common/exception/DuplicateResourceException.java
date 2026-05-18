package com.labo.anapath.common.exception;

/**
 * Exception levée lors d'une tentative de création d'une ressource déjà existante.
 * <p>
 * Typiquement utilisée lors de violations de contraintes d'unicité (ex. email déjà utilisé,
 * code de prélèvement dupliqué). Le {@link GlobalExceptionHandler} la traduit en
 * réponse HTTP {@code 409 Conflict}.
 * </p>
 */
public class DuplicateResourceException extends RuntimeException {

    /**
     * Construit une {@link DuplicateResourceException} avec un message descriptif.
     *
     * @param message description de l'unicité violée
     */
    public DuplicateResourceException(String message) {
        super(message);
    }
}
