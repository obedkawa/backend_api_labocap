package com.labo.anapath.report;

import com.labo.anapath.common.audit.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "test_pathology_macros")
@SQLDelete(sql = "UPDATE test_pathology_macros SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
@NoArgsConstructor
public class TestPathologyMacro extends AuditableEntity {

    @Column(name = "title", nullable = false, length = 300)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "test_order_id")
    private UUID testOrderId;

    @Column(name = "circulation")
    private Boolean circulation = false;

    @Column(name = "embedding")
    private Boolean embedding = false;

    @Column(name = "microtomy_spreading")
    private Boolean microtomySpreading = false;

    @Column(name = "staining")
    private Boolean staining = false;

    @Column(name = "mounting")
    private Boolean mounting = false;

    @Column(name = "macro_date")
    private LocalDate macroDate;

    public void setAllStepsTrue() {
        this.circulation = true;
        this.embedding = true;
        this.microtomySpreading = true;
        this.staining = true;
        this.mounting = true;
    }
}
