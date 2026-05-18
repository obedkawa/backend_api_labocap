package com.labo.anapath.branch;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository Spring Data JPA pour l'accès aux données des agences ({@link Branch}).
 * <p>
 * Fournit les requêtes personnalisées nécessaires au contrôle d'unicité du nom
 * lors de la création et de la mise à jour.
 * </p>
 */
@Repository
public interface BranchRepository extends JpaRepository<Branch, UUID> {

    /**
     * Vérifie qu'il n'existe pas déjà une agence portant ce nom (insensible à la casse).
     *
     * @param name nom à vérifier
     * @return {@code true} si une agence avec ce nom existe déjà
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Vérifie l'unicité du nom en excluant l'agence dont l'identifiant est fourni.
     * Utilisé lors d'une mise à jour pour ne pas bloquer l'agence sur son propre nom.
     *
     * @param name nom à vérifier
     * @param id   identifiant de l'agence à exclure de la vérification
     * @return {@code true} si une autre agence porte déjà ce nom
     */
    boolean existsByNameIgnoreCaseAndIdNot(String name, UUID id);
}
