package com.labo.anapath.hr;

import com.labo.anapath.common.dto.PageResponse;

import java.util.UUID;

/**
 * Interface de service définissant les opérations métier sur les employés du laboratoire.
 */
public interface EmployeeService {

    /**
     * Retourne la liste paginée des employés d'une filiale.
     *
     * @param page     numéro de page
     * @param size     taille de la page
     * @param branchId identifiant de la filiale
     * @return page d'employés
     */
    PageResponse<EmployeeResponseDto> findAll(int page, int size, UUID branchId);

    /**
     * Retourne un employé par son identifiant.
     *
     * @param id identifiant UUID de l'employé
     * @return l'employé correspondant
     * @throws com.labo.anapath.common.exception.ResourceNotFoundException si introuvable
     */
    EmployeeResponseDto findById(UUID id);

    /**
     * Crée un nouvel employé rattaché à une filiale.
     *
     * @param dto      données de l'employé
     * @param branchId identifiant de la filiale
     * @return l'employé créé
     */
    EmployeeResponseDto create(EmployeeRequestDto dto, UUID branchId);

    /**
     * Met à jour les informations d'un employé existant.
     *
     * @param id  identifiant UUID de l'employé à modifier
     * @param dto nouvelles données
     * @return l'employé mis à jour
     */
    EmployeeResponseDto update(UUID id, EmployeeRequestDto dto);

    /**
     * Supprime (logiquement) un employé.
     *
     * @param id identifiant UUID de l'employé à supprimer
     */
    void delete(UUID id);
}
