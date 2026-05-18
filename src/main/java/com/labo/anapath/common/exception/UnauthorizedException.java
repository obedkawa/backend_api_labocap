package com.labo.anapath.common.exception;

/**
 * Exception levée lorsqu'un utilisateur tente d'accéder à une ressource sans
 * les droits d'authentification requis (token absent, invalide ou révoqué).
 * <p>
 * Le {@link GlobalExceptionHandler} la traduit en réponse HTTP {@code 401 Unauthorized}.
 * Ne pas confondre avec {@link org.springframework.security.access.AccessDeniedException}
 * qui correspond à un accès interdit ({@code 403 Forbidden}).
 * </p>
 */
public class UnauthorizedException extends RuntimeException {

    /**
     * Construit une {@link UnauthorizedException} avec un message descriptif.
     *
     * @param message explication de l'échec d'authentification
     */
    public UnauthorizedException(String message) {
        super(message);
    }
}
