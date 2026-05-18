package com.labo.anapath.finance;

import com.labo.anapath.common.dto.PageResponse;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.UUID;

public interface BankService {

    PageResponse<BankResponseDto> findAll(int page, int size, UUID branchId);

    BankResponseDto findById(UUID id);

    BankResponseDto create(BankRequestDto dto, UUID branchId);

    BankResponseDto update(UUID id, BankRequestDto dto);

    void delete(UUID id);

    BankDepositResponseDto createDeposit(BankDepositRequestDto dto, UUID branchId, UUID userId);

    PageResponse<BankDepositResponseDto> findDeposits(int page, int size, UUID branchId, UUID bankId, LocalDate date);
}
