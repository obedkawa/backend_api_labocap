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

import java.math.BigDecimal;
import java.util.UUID;

// ATTENTION: nom de table "expence_details" (avec 'c', pas 's') — typo métier issu de Laravel
@Entity
@Table(name = "expence_details")
@Getter
@Setter
@NoArgsConstructor
public class ExpenceDetail extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_id", nullable = false)
    private Expense expense;

    @Column(name = "article_name", length = 200)
    private String articleName;

    @Column(name = "article_id")
    private UUID articleId;

    @Column(name = "quantity", precision = 10, scale = 2)
    private BigDecimal quantity;

    @Column(name = "unit_price", precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "line_amount", precision = 12, scale = 2)
    private BigDecimal lineAmount;
}
