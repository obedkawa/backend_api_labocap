package com.labo.anapath.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository JPA pour l'entité {@link User}.
 *
 * <p>Les requêtes de recherche et d'existence sont automatiquement filtrées
 * par la restriction Hibernate {@code deleted_at IS NULL} définie sur l'entité,
 * garantissant que les utilisateurs supprimés logiquement sont invisibles.</p>
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Recherche un utilisateur actif par son adresse e-mail.
     *
     * @param email adresse e-mail à rechercher
     * @return un {@link Optional} contenant l'utilisateur trouvé, ou vide
     */
    Optional<User> findByEmail(String email);

    /**
     * Vérifie si un utilisateur actif existe avec l'adresse e-mail donnée.
     * Utilisé pour détecter les doublons avant création ou modification.
     *
     * @param email adresse e-mail à vérifier
     * @return {@code true} si l'e-mail est déjà utilisé
     */
    boolean existsByEmail(String email);

    /**
     * Retourne la liste paginée des utilisateurs d'une succursale donnée.
     *
     * @param branchId identifiant de la succursale
     * @param pageable paramètres de pagination et de tri
     * @return page d'utilisateurs
     */
    Page<User> findByBranchId(UUID branchId, Pageable pageable);

    /**
     * Vérifie si au moins un utilisateur est rattaché à la succursale donnée.
     *
     * @param branchId identifiant de la succursale
     * @return {@code true} si la succursale possède des utilisateurs
     */
    boolean existsByBranchId(UUID branchId);
}
