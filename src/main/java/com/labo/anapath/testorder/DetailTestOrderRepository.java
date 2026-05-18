package com.labo.anapath.testorder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository Spring Data JPA pour l'entité {@link DetailTestOrder}.
 *
 * <p>Les opérations de création et de suppression des détails passent principalement
 * par la cascade JPA depuis {@link TestOrder} (orphanRemoval). Ce repository
 * est exposé pour les accès directs ponctuels si nécessaire.
 */
@Repository
public interface DetailTestOrderRepository extends JpaRepository<DetailTestOrder, UUID> {
}
