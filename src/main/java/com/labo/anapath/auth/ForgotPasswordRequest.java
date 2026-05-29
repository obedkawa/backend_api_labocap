package com.labo.anapath.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO de requête pour l'initiation de la réinitialisation de mot de passe.
 * <p>
 * Reçu sur {@code POST /api/v1/auth/forgot-password}.
 * </p>
 */
@Getter
@Setter
public class ForgotPasswordRequest {

    /** Adresse e-mail du compte dont le mot de passe doit être réinitialisé. */
    @Email(message = "L'email doit être valide")
    @NotBlank(message = "L'email est obligatoire")
    private String email;
}
