package com.labo.anapath.contract;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository JPA pour l'entité {@link DetailsContrat}.
 *
 * <p>Les lignes tarifaires sont gérées en cascade depuis {@link Contrat}
 * (orphanRemoval = true) ; ce repository est disponible pour des accès
 * directs ponctuels si nécessaire.</p>
 */
@Repository
public interface DetailsContratRepository extends JpaRepository<DetailsContrat, UUID> {
}
