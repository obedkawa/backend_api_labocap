package com.labo.anapath.role;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository JPA pour l'entité {@link Role}.
 *
 * <p>Les requêtes sont automatiquement filtrées par {@code deleted_at IS NULL}
 * grâce à la restriction Hibernate définie sur l'entité.</p>
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

    /**
     * Retourne la liste paginée des rôles d'une succursale donnée.
     *
     * @param branchId identifiant de la succursale
     * @param pageable paramètres de pagination et de tri
     * @return page de rôles
     */
    Page<Role> findByBranchId(UUID branchId, Pageable pageable);

    /**
     * Recherche un rôle par son slug et sa succursale.
     * Utilisé pour charger un rôle spécifique dans un contexte multi-tenant.
     *
     * @param slug     slug du rôle (ex. {@code administrateur})
     * @param branchId identifiant de la succursale
     * @return un {@link Optional} contenant le rôle trouvé, ou vide
     */
    Optional<Role> findBySlugAndBranchId(String slug, UUID branchId);

    /**
     * Vérifie si un rôle avec ce slug existe (toutes succursales confondues).
     * Utilisé pour garantir l'unicité globale du slug avant création ou mise à jour.
     *
     * @param slug slug à vérifier
     * @return {@code true} si le slug est déjà utilisé
     */
    boolean existsBySlug(String slug);
}
