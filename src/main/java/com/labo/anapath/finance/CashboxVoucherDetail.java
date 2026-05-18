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

@Entity
@Table(name = "cashbox_voucher_details")
@Getter
@Setter
@NoArgsConstructor
public class CashboxVoucherDetail extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cashbox_voucher_id", nullable = false)
    private CashboxVoucher cashboxVoucher;

    @Column(name = "item_name", length = 200)
    private String itemName;

    @Column(name = "item_id")
    private UUID itemId;

    @Column(name = "quantity", precision = 10, scale = 2)
    private BigDecimal quantity;

    @Column(name = "unit_price", precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "line_amount", precision = 12, scale = 2)
    private BigDecimal lineAmount;
}
