package com.labo.anapath.finance;

import com.labo.anapath.common.audit.AuditableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "cashbox_vouchers")
@SQLDelete(sql = "UPDATE cashbox_vouchers SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
@NoArgsConstructor
public class CashboxVoucher extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cashbox_id")
    private Cashbox cashbox;

    @Column(name = "code", length = 50)
    private String code;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount = BigDecimal.ZERO;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "en attente";

    @Column(name = "supplier_id")
    private UUID supplierId;

    @Column(name = "expense_category_id")
    private UUID expenseCategoryId;

    @Column(name = "ticket_file", length = 500)
    private String ticketFile;

    @OneToMany(mappedBy = "cashboxVoucher", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<CashboxVoucherDetail> details = new ArrayList<>();
}
