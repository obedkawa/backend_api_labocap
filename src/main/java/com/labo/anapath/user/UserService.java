package com.labo.anapath.user;

import com.labo.anapath.common.dto.PageResponse;

import java.util.UUID;

/**
 * Interface de service métier pour la gestion des utilisateurs.
 *
 * <p>Définit les opérations disponibles sur les comptes utilisateurs :
 * consultation, création, mise à jour, suppression, gestion du statut
 * et changement de mot de passe.</p>
 */
public interface UserService {

    /**
     * Retourne la liste paginée des utilisateurs d'une succursale.
     *
     * @param page     numéro de page (0-indexé)
     * @param size     taille de la page
     * @param branchId identifiant de la succursale
     * @return page de DTOs utilisateurs
     */
    PageResponse<UserResponseDto> findAll(int page, int size, UUID branchId);

    /**
     * Recherche un utilisateur par son identifiant.
     *
     * @param id identifiant UUID de l'utilisateur
     * @return le DTO de l'utilisateur trouvé
     * @throws com.labo.anapath.common.exception.ResourceNotFoundException si l'utilisateur n'existe pas
     */
    UserResponseDto findById(UUID id);

    /**
     * Crée un nouvel utilisateur dans la succursale spécifiée.
     *
     * @param dto      données de création
     * @param branchId identifiant de la succursale de rattachement
     * @return le DTO de l'utilisateur créé
     * @throws com.labo.anapath.common.exception.DuplicateResourceException si l'e-mail est déjà utilisé
     */
    UserResponseDto create(UserRequestDto dto, UUID branchId);

    /**
     * Met à jour les informations d'un utilisateur existant.
     *
     * @param id  identifiant UUID de l'utilisateur
     * @param dto nouvelles données
     * @return le DTO mis à jour
     */
    UserResponseDto update(UUID id, UserRequestDto dto);

    /**
     * Supprime logiquement un utilisateur (soft-delete).
     *
     * @param id identifiant UUID de l'utilisateur à supprimer
     */
    void delete(UUID id);

    /**
     * Change le mot de passe d'un utilisateur après vérification de l'ancien.
     *
     * @param id      identifiant UUID de l'utilisateur
     * @param request objet contenant l'ancien et le nouveau mot de passe
     * @throws com.labo.anapath.common.exception.BusinessException si l'ancien mot de passe est incorrect
     */
    void updatePassword(UUID id, UpdatePasswordRequest request);

    /**
     * Bascule le statut actif/inactif d'un utilisateur.
     * En cas de désactivation, la session et le 2FA sont également réinitialisés.
     *
     * @param id identifiant UUID de l'utilisateur
     */
    void toggleStatus(UUID id);
}
