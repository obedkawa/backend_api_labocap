package com.labo.anapath.report;

import com.labo.anapath.common.audit.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "appel_by_reports")
@Getter
@Setter
@NoArgsConstructor
public class AppelByReport extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;

    /** Identifiant retourné par l'API OurVoice après un appel vocal. */
    @Column(name = "appel_id", nullable = false, length = 255)
    private String appelId;
}
