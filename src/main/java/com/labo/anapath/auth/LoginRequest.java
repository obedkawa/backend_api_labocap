package com.labo.anapath.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO de requête pour l'authentification par identifiants.
 * <p>
 * Reçu sur {@code POST /api/v1/auth/login}. Les contraintes de validation Bean Validation
 * sont vérifiées avant l'appel au service.
 * </p>
 */
@Getter
@Setter
public class LoginRequest {

    /** Adresse email de l'utilisateur, servant d'identifiant de connexion. */
    @Email(message = "L'email doit être valide")
    @NotBlank(message = "L'email est obligatoire")
    private String email;

    /** Mot de passe en clair, comparé au hash BCrypt stocké en base. */
    @NotBlank(message = "Le mot de passe est obligatoire")
    private String password;
}
