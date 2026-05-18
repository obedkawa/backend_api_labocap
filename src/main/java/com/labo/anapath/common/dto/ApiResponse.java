package com.labo.anapath.common.dto;

import java.time.LocalDateTime;

/**
 * Enveloppe générique pour toutes les réponses JSON de l'API.
 * <p>
 * Chaque réponse expose systématiquement :
 * <ul>
 *   <li>{@code success} — indique si la requête s'est terminée avec succès ;</li>
 *   <li>{@code message} — message lisible par l'appelant (ex. "OK", libellé d'erreur) ;</li>
 *   <li>{@code data} — corps de la réponse, {@code null} en cas d'erreur ;</li>
 *   <li>{@code timestamp} — horodatage de la réponse côté serveur.</li>
 * </ul>
 * </p>
 *
 * @param <T> type du corps de données retourné
 */
public record ApiResponse<T>(boolean success, String message, T data, LocalDateTime timestamp) {

    /**
     * Crée une réponse de succès avec le message par défaut "OK".
     *
     * @param <T>  type des données
     * @param data corps de la réponse
     * @return une {@link ApiResponse} avec {@code success = true}
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "OK", data, LocalDateTime.now());
    }

    /**
     * Crée une réponse de succès avec un message personnalisé.
     *
     * @param <T>     type des données
     * @param message message descriptif du succès
     * @param data    corps de la réponse
     * @return une {@link ApiResponse} avec {@code success = true}
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, LocalDateTime.now());
    }

    /**
     * Crée une réponse d'erreur sans données.
     *
     * @param <T>     type paramétré (généralement {@link Object})
     * @param message description de l'erreur
     * @return une {@link ApiResponse} avec {@code success = false} et {@code data = null}
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, LocalDateTime.now());
    }
}
