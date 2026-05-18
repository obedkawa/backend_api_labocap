package com.labo.anapath.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO de requête pour les opérations 2FA nécessitant uniquement un code TOTP.
 * <p>
 * Utilisé sur les endpoints {@code POST /api/v1/auth/2fa/verify} (activation)
 * et {@code POST /api/v1/auth/2fa/disable} (désactivation). Dans ces cas,
 * l'utilisateur est déjà pleinement authentifié (token d'accès complet requis).
 * </p>
 */
@Getter
@Setter
public class TwoFaCodeRequest {

    /** Code TOTP à 6 chiffres généré par Google Authenticator. */
    @NotBlank(message = "Le code TOTP est obligatoire")
    @Pattern(regexp = "^\\d{6}$", message = "Le code TOTP doit contenir exactement 6 chiffres")
    private String code;
}
