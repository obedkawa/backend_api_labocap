package com.labo.anapath.finance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository JPA pour l'entité {@link InvoiceDetail}.
 *
 * <p>Les lignes de détail sont gérées en cascade depuis {@link Invoice}
 * (orphanRemoval = true) ; ce repository est disponible pour des
 * accès directs ponctuels si nécessaire.</p>
 */
@Repository
public interface InvoiceDetailRepository extends JpaRepository<InvoiceDetail, UUID> {
}
