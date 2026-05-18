package com.labo.anapath.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO de requête pour le challenge 2FA lors de la connexion.
 * <p>
 * Reçu sur l'endpoint public {@code POST /api/v1/auth/2fa/challenge}.
 * Contient à la fois le token temporaire de challenge émis lors du login
 * et le code TOTP généré par l'application Google Authenticator de l'utilisateur.
 * </p>
 */
@Getter
@Setter
public class TwoFactorVerifyRequest {

    /** Code TOTP à 6 chiffres généré par Google Authenticator. */
    @NotBlank(message = "Le code 2FA est obligatoire")
    @Pattern(regexp = "^\\d{6}$", message = "Le code TOTP doit contenir exactement 6 chiffres")
    private String code;

    /** Token temporaire de type {@code 2fa-challenge} reçu lors du login, valide 5 minutes. */
    @NotBlank(message = "Le token temporaire est obligatoire")
    private String tempToken;
}
