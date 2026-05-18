package com.labo.anapath.contract;

import com.labo.anapath.test.LabTest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "details_contrats")
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class DetailsContrat {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrat_id", nullable = false)
    private Contrat contrat;

    // Nullable : les lignes par catégorie n'ont pas de test individuel
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lab_test_id")
    private LabTest labTest;

    // Nullable : les lignes par catégorie utilisent pourcentage, pas un prix fixe
    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    // Pourcentage de remise sur catégorie (null pour les lignes de test individuel)
    @Column(name = "pourcentage", precision = 5, scale = 2)
    private BigDecimal pourcentage;

    // Montant de remise fixe sur test individuel
    @Column(name = "amount_remise", precision = 10, scale = 2)
    private BigDecimal amountRemise;

    // Prix après remise sur test individuel
    @Column(name = "amount_after_remise", precision = 10, scale = 2)
    private BigDecimal amountAfterRemise;

    // Catégorie de test (obligatoire pour les lignes par catégorie, aussi présent pour test individuel)
    @Column(name = "category_test_id")
    private UUID categoryTestId;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
