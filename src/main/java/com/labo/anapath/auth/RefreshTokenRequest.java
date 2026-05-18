package com.labo.anapath.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO de requête pour le renouvellement du token d'accès.
 * <p>
 * Reçu sur {@code POST /api/v1/auth/refresh}. Le refresh token fourni doit être
 * valide, non expiré et non révoqué (non blacklisté).
 * </p>
 */
@Getter
@Setter
public class RefreshTokenRequest {

    /** Token de rafraîchissement JWT émis lors de la connexion initiale. */
    @NotBlank(message = "Le refresh token est obligatoire")
    private String refreshToken;
}
