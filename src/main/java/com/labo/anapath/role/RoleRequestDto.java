package com.labo.anapath.role;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

/**
 * DTO de requête pour la création et la mise à jour d'un rôle.
 *
 * <p>Le slug est optionnel à la saisie : s'il n'est pas fourni,
 * le service le génère automatiquement depuis le nom (normalisation
 * minuscules, suppression des accents et caractères spéciaux).</p>
 */
@Getter
@Setter
public class RoleRequestDto {

    /** Nom lisible du rôle (obligatoire, ex. : "Administrateur"). */
    @NotBlank(message = "Le nom du rôle est obligatoire")
    private String name;

    /**
     * Slug technique du rôle. Si absent, il est déduit du nom par le service.
     * Doit être unique dans le système.
     */
    private String slug;

    /** Description optionnelle des responsabilités associées à ce rôle. */
    private String description;

    /** Liste des identifiants de permissions à attribuer à ce rôle. */
    private List<UUID> permissionIds;
}
