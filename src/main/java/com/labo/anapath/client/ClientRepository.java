package com.labo.anapath.client;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository Spring Data JPA pour l'accès aux données des clients institutionnels ({@link Client}).
 * <p>
 * Les requêtes filtrent systématiquement par {@code branchId} pour garantir l'isolation
 * des données entre agences. Les méthodes de vérification d'unicité couvrent à la fois
 * le nom (par agence) et le numéro IFU (global, car l'IFU est un identifiant fiscal unique).
 * </p>
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, UUID> {

    /**
     * Retourne les clients d'une agence donnée, paginés.
     *
     * @param branchId identifiant de l'agence
     * @param pageable paramètres de pagination et de tri
     * @return page de clients
     */
    Page<Client> findByBranchId(UUID branchId, Pageable pageable);

    /**
     * Vérifie qu'aucun autre client de la même agence n'a déjà ce nom.
     *
     * @param name     nom à vérifier (insensible à la casse)
     * @param branchId identifiant de l'agence
     * @return {@code true} si un doublon existe
     */
    boolean existsByNameIgnoreCaseAndBranchId(String name, UUID branchId);

    /**
     * Vérifie l'unicité du nom au sein d'une agence, en excluant le client identifié.
     * Utilisé lors d'une mise à jour pour ne pas bloquer le client sur son propre nom.
     *
     * @param name     nom à vérifier
     * @param branchId identifiant de l'agence
     * @param id       identifiant du client à exclure
     * @return {@code true} si un autre client de l'agence porte déjà ce nom
     */
    boolean existsByNameIgnoreCaseAndBranchIdAndIdNot(String name, UUID branchId, UUID id);

    /**
     * Vérifie qu'aucun client (toutes agences confondues) n'utilise déjà cet IFU.
     * L'IFU est un identifiant fiscal national unique, donc la vérification est globale.
     *
     * @param ifu numéro IFU à vérifier
     * @return {@code true} si l'IFU est déjà enregistré
     */
    boolean existsByIfu(String ifu);

    /**
     * Vérifie l'unicité de l'IFU en excluant le client identifié.
     *
     * @param ifu numéro IFU à vérifier
     * @param id  identifiant du client à exclure
     * @return {@code true} si un autre client utilise déjà cet IFU
     */
    boolean existsByIfuAndIdNot(String ifu, UUID id);

    /**
     * Recherche les clients d'une agence dont le nom contient la chaîne fournie
     * (insensible à la casse).
     *
     * @param name     terme de recherche partiel
     * @param branchId identifiant de l'agence
     * @return liste des clients correspondants
     */
    List<Client> findByNameContainingIgnoreCaseAndBranchId(String name, UUID branchId);
}
