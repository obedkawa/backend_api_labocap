package com.labo.anapath.testorder;

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
@Table(name = "test_order_assignment_details")
@Getter
@Setter
@NoArgsConstructor
public class TestOrderAssignmentDetail extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_order_assignment_id", nullable = false)
    private TestOrderAssignment testOrderAssignment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_order_id")
    private TestOrder testOrder;

    @Column(name = "test_order_code", length = 50)
    private String testOrderCode;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;
}
