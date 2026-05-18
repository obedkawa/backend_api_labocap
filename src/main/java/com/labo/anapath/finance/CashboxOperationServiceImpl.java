package com.labo.anapath.finance;

import com.labo.anapath.common.dto.PageResponse;
import com.labo.anapath.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CashboxOperationServiceImpl implements CashboxOperationService {

    private final CashboxOperationRepository cashboxOperationRepository;
    private final CashboxRepository cashboxRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CashboxOperationResponseDto> findAll(int page, int size, UUID branchId,
                                                              UUID cashboxId, String type, LocalDate date) {
        return PageResponse.of(cashboxOperationRepository.findWithFilters(
                branchId, cashboxId, type, date, PageRequest.of(page, size))
                .map(this::toDto));
    }

    @Override
    @Transactional
    public CashboxOperationResponseDto create(CashboxOperationCreateDto dto, UUID branchId) {
        Cashbox cashbox = cashboxRepository.findById(dto.getCashboxId())
                .orElseThrow(() -> new ResourceNotFoundException("Caisse", dto.getCashboxId()));

        CashboxOperation operation = new CashboxOperation();
        operation.setBranchId(branchId);
        operation.setCashbox(cashbox);
        operation.setAmount(dto.getAmount());
        operation.setType(dto.getType());
        operation.setDescription(dto.getDescription());
        operation.setOperationDate(dto.getOperationDate() != null ? dto.getOperationDate() : LocalDate.now());
        operation.setReference(dto.getReference());
        operation.setChequeNumber(dto.getChequeNumber());

        // AC7 — mise à jour atomique du solde
        if ("CREDIT".equals(dto.getType())) {
            cashbox.setBalance(cashbox.getBalance().add(dto.getAmount()));
        } else {
            cashbox.setBalance(cashbox.getBalance().subtract(dto.getAmount()));
        }
        cashboxRepository.save(cashbox);

        return toDto(cashboxOperationRepository.save(operation));
    }

    private CashboxOperationResponseDto toDto(CashboxOperation o) {
        return new CashboxOperationResponseDto(
                o.getId(),
                o.getCashbox() != null ? o.getCashbox().getId() : null,
                o.getAmount(),
                o.getType(),
                o.getDescription(),
                o.getOperationDate(),
                o.getBranchId(),
                o.getCreatedAt()
        );
    }
}
