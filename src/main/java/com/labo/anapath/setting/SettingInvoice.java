package com.labo.anapath.setting;

import com.labo.anapath.common.audit.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "setting_invoices")
@SQLDelete(sql = "UPDATE setting_invoices SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
@NoArgsConstructor
public class SettingInvoice extends AuditableEntity {

    @Column(name = "ifu", length = 50)
    private String ifu;

    @Column(name = "token", columnDefinition = "TEXT")
    private String token;

    @Column(name = "status", nullable = false)
    private Boolean status = false;
}
