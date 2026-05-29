package com.labo.anapath.role;

import com.labo.anapath.common.dto.PageResponse;

import java.util.List;
import java.util.UUID;

/**
 * Interface de service métier pour la gestion des rôles RBAC.
 *
 * <p>Définit les opérations disponibles : consultation, création,
 * mise à jour, suppression et assignation de permissions à un rôle.</p>
 */
public interface RoleService {

    /**
     * Retourne la liste paginée des rôles d'une succursale.
     *
     * @param page     numéro de page (0-indexé)
     * @param size     taille de la page
     * @param branchId identifiant de la succursale
     * @return page de DTOs de rôles
     */
    PageResponse<RoleResponseDto> findAll(int page, int size, UUID branchId);

    /**
     * Recherche un rôle par son identifiant, filtré par succursale.
     *
     * @param id       identifiant UUID du rôle
     * @param branchId identifiant de la succursale (isolation multi-tenant)
     * @return le DTO du rôle trouvé
     * @throws com.labo.anapath.common.exception.ResourceNotFoundException si le rôle n'existe pas ou n'appartient pas à la succursale
     */
    RoleResponseDto findById(UUID id, UUID branchId);

    /**
     * Crée un nouveau rôle dans la succursale spécifiée.
     * Le slug est généré automatiquement depuis le nom du rôle.
     *
     * @param dto      données de création
     * @param branchId identifiant de la succursale de rattachement
     * @return le DTO du rôle créé
     * @throws com.labo.anapath.common.exception.DuplicateResourceException si le slug est déjà utilisé
     */
    RoleResponseDto create(RoleRequestDto dto, UUID branchId);

    /**
     * Met à jour les informations d'un rôle existant, vérifié dans la succursale de l'appelant.
     * Le slug est recalculé depuis le nouveau nom.
     *
     * @param id       identifiant UUID du rôle
     * @param dto      nouvelles données
     * @param branchId identifiant de la succursale (isolation multi-tenant)
     * @return le DTO mis à jour
     */
    RoleResponseDto update(UUID id, RoleRequestDto dto, UUID branchId);

    /**
     * Supprime logiquement un rôle (soft-delete), vérifié dans la succursale de l'appelant.
     *
     * @param id       identifiant UUID du rôle à supprimer
     * @param branchId identifiant de la succursale (isolation multi-tenant)
     */
    void delete(UUID id, UUID branchId);

    /**
     * Remplace intégralement la liste des permissions d'un rôle, vérifié dans la succursale de l'appelant.
     *
     * @param roleId        identifiant UUID du rôle
     * @param permissionIds liste des identifiants de permissions à assigner
     * @param branchId      identifiant de la succursale (isolation multi-tenant)
     * @return le DTO du rôle avec ses nouvelles permissions
     */
    RoleResponseDto assignPermissions(UUID roleId, List<UUID> permissionIds, UUID branchId);
}
