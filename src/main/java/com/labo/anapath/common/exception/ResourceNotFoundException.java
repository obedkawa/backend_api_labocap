package com.labo.anapath.common.exception;

import java.util.UUID;

/**
 * Exception levée lorsqu'une ressource recherchée est introuvable en base de données.
 * <p>
 * Le {@link GlobalExceptionHandler} la traduit en réponse HTTP {@code 404 Not Found}.
 * </p>
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Construit l'exception à partir du nom de la ressource et de son identifiant UUID.
     * <p>
     * Produit un message du type : {@code "User not found with id: <uuid>"}.
     * </p>
     *
     * @param resourceName nom de la ressource non trouvée (ex. "User", "Examen")
     * @param id           UUID de la ressource recherchée
     */
    public ResourceNotFoundException(String resourceName, UUID id) {
        super(resourceName + " not found with id: " + id);
    }

    /**
     * Construit l'exception avec un message libre.
     *
     * @param message description de la ressource introuvable
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
