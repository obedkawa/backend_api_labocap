package com.labo.anapath.role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository JPA pour l'entité {@link Permission}.
 *
 * <p>Les permissions étant globales (non scopées par succursale),
 * aucun filtre de type {@code branchId} n'est nécessaire ici.</p>
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, UUID> {

    /**
     * Recherche une permission par son slug technique.
     * Utilisé notamment lors de la vérification des droits ou du seeding.
     *
     * @param slug slug de la permission (ex. {@code manage-users})
     * @return un {@link Optional} contenant la permission trouvée, ou vide
     */
    Optional<Permission> findBySlug(String slug);
}
