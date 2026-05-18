package com.labo.anapath.testorder;

import com.labo.anapath.common.audit.AuditableEntity;
import com.labo.anapath.user.User;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "test_order_assignments")
@SQLDelete(sql = "UPDATE test_order_assignments SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
@NoArgsConstructor
public class TestOrderAssignment extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "code", length = 50)
    private String code;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "date")
    private LocalDate date;

    @OneToMany(mappedBy = "testOrderAssignment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TestOrderAssignmentDetail> details = new ArrayList<>();
}
