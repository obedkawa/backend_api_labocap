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
@Table(name = "expenses")
@SQLDelete(sql = "UPDATE expenses SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
@NoArgsConstructor
public class Expense extends AuditableEntity {

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount = BigDecimal.ZERO;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "item_name", length = 200)
    private String itemName;

    @Column(name = "item_id")
    private UUID itemId;

    @Column(name = "total_amount", precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "unit_price", precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "quantity", precision = 10, scale = 2)
    private BigDecimal quantity;

    @Column(name = "supplier_id")
    private UUID supplierId;

    // Note: colonne nommée "expense_categorie_id" (avec 'ie') — typo issu de Laravel
    @Column(name = "expense_categorie_id")
    private UUID expenseCategorieId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_categorie_id", insertable = false, updatable = false)
    private ExpenseCategory expenseCategory;

    @Column(name = "receipt", length = 500)
    private String receipt;

    @Column(name = "cashbox_voucher_id")
    private UUID cashboxVoucherId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cashbox_voucher_id", insertable = false, updatable = false)
    private CashboxVoucher cashboxVoucher;

    // 0 = non payé, 1 = payé, 2 = payé + stock mis à jour
    @Column(name = "paid", nullable = false)
    private Integer paid = 0;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "invoice_number", length = 100)
    private String invoiceNumber;

    @Column(name = "payment", length = 20)
    private String payment;
}
