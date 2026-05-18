package com.labo.anapath.finance;

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
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "cashbox_operations")
@SQLDelete(sql = "UPDATE cashbox_operations SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
@NoArgsConstructor
public class CashboxOperation extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cashbox_id", nullable = false)
    private Cashbox cashbox;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    /** CREDIT ou DEBIT */
    @Column(name = "type", nullable = false, length = 10)
    private String type;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "operation_date", nullable = false)
    private LocalDate operationDate;

    @Column(name = "reference", length = 100)
    private String reference;

    @Column(name = "cheque_number", length = 50)
    private String chequeNumber;

    @Column(name = "attachement", length = 500)
    private String attachement;

    @Column(name = "bank_id")
    private UUID bankId;

    @Column(name = "invoice_id")
    private UUID invoiceId;

    @Column(name = "payment_method", length = 20)
    private String paymentMethod;
}
