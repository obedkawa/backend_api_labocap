package com.labo.anapath.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO de requête pour le renvoi d'un code OTP de second facteur.
 * <p>
 * Reçu sur {@code POST /api/v1/auth/resend-2fa}.
 * </p>
 */
@Getter
@Setter
public class Resend2FARequest {

    /** Adresse e-mail du compte pour lequel renvoyer le code OTP. */
    @Email(message = "L'email doit être valide")
    @NotBlank(message = "L'email est obligatoire")
    private String email;
}
