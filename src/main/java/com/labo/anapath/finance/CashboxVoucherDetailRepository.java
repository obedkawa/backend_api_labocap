package com.labo.anapath.finance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CashboxVoucherDetailRepository extends JpaRepository<CashboxVoucherDetail, UUID> {

    @org.springframework.data.jpa.repository.Query("SELECT d FROM CashboxVoucherDetail d WHERE d.cashboxVoucher.id = :voucherId")
    List<CashboxVoucherDetail> findByCashboxVoucherId(@org.springframework.data.repository.query.Param("voucherId") UUID voucherId);
}
