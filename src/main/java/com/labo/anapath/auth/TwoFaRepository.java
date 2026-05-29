package com.labo.anapath.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TwoFaRepository extends JpaRepository<TwoFa, UUID> {

    Optional<TwoFa> findByUserId(UUID userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM TwoFa t WHERE t.userId = :userId")
    void deleteByUserId(@Param("userId") UUID userId);
}
