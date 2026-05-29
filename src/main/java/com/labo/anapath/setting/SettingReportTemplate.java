package com.labo.anapath.setting;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entité représentant un modèle de mise en page pour les rapports PDF du laboratoire.
 * <p>
 * Chaque modèle définit l'en-tête, le pied de page et le logo utilisés
 * lors de la génération des documents (comptes rendus, résultats d'analyses, etc.).
 * Cette entité gère ses propres champs d'audit via {@link AuditingEntityListener}.
 * </p>
 */
@Entity
@Table(name = "setting_report_templates")
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class SettingReportTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "branch_id", nullable = false)
    private UUID branchId;

    @Column(name = "name", length = 200)
    private String name;

    @Column(name = "header", columnDefinition = "TEXT")
    private String header;

    @Column(name = "footer", columnDefinition = "TEXT")
    private String footer;

    @Column(name = "logo_path", length = 500)
    private String logoPath;

    /** Titre du template (champ Laravel). */
    @Column(name = "title", length = 255)
    private String title;

    /** Description (champ Laravel). */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /** Détail JSON (champ Laravel). */
    @Column(name = "detail", columnDefinition = "TEXT")
    private String detail;

    /** Contenu HTML du template (champ Laravel). */
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
