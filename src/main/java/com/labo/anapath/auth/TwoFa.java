package com.labo.anapath.auth;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "two_fas")
@Getter
@Setter
@NoArgsConstructor
public class TwoFa {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String code;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(name = "branch_id")
    private UUID branchId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public TwoFa(UUID userId, UUID branchId, String hashedCode) {
        this.userId = userId;
        this.branchId = branchId;
        this.code = hashedCode;
    }
}
