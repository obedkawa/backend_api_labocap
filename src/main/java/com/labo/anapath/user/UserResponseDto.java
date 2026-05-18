package com.labo.anapath.user;

import com.labo.anapath.role.RoleResponseDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO de réponse représentant un utilisateur exposé par l'API.
 *
 * <p>Les informations sensibles (mot de passe, secret 2FA, OTP) sont
 * intentionnellement absentes de ce record.</p>
 *
 * @param id        identifiant unique de l'utilisateur
 * @param firstname prénom
 * @param lastname  nom de famille
 * @param email     adresse e-mail
 * @param phone     numéro de téléphone
 * @param isActive  indique si le compte est actif
 * @param branchId  identifiant de la succursale de rattachement
 * @param createdAt date et heure de création du compte
 * @param roles     liste des rôles attribués à l'utilisateur
 */
public record UserResponseDto(
        UUID id,
        String firstname,
        String lastname,
        String email,
        String phone,
        boolean isActive,
        UUID branchId,
        LocalDateTime createdAt,
        List<RoleResponseDto> roles
) {}
