package com.labo.anapath.finance;

import com.labo.anapath.common.dto.PageResponse;
import com.labo.anapath.common.exception.BusinessException;
import com.labo.anapath.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BankServiceImpl implements BankService {

    private final BankRepository bankRepository;
    private final BankDepositRepository bankDepositRepository;
    private final CashboxRepository cashboxRepository;
    private final CashboxOperationRepository cashboxOperationRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BankResponseDto> findAll(int page, int size, UUID branchId) {
        return PageResponse.of(bankRepository.findByBranchId(branchId,
                PageRequest.of(page, size, Sort.by("name").ascending()))
                .map(this::toDto));
    }

    @Override
    @Transactional(readOnly = true)
    public BankResponseDto findById(UUID id) {
        return toDto(bankRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bank", id)));
    }

    @Override
    @Transactional
    public BankResponseDto create(BankRequestDto dto, UUID branchId) {
        Bank bank = new Bank();
        bank.setBranchId(branchId);
        bank.setName(dto.getName());
        bank.setAccountNumber(dto.getAccountNumber());
        bank.setDescription(dto.getDescription());
        return toDto(bankRepository.save(bank));
    }

    @Override
    @Transactional
    public BankResponseDto update(UUID id, BankRequestDto dto) {
        Bank bank = bankRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bank", id));
        bank.setName(dto.getName());
        bank.setAccountNumber(dto.getAccountNumber());
        bank.setDescription(dto.getDescription());
        return toDto(bankRepository.save(bank));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Bank bank = bankRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bank", id));
        bankRepository.delete(bank);
    }

    @Override
    @Transactional
    public BankDepositResponseDto createDeposit(BankDepositRequestDto dto, UUID branchId, UUID userId) {
        Bank bank = bankRepository.findById(dto.getBankId())
                .orElseThrow(() -> new ResourceNotFoundException("Bank", dto.getBankId()));

        // R2: toujours par type, jamais par ID hardcodé
        Cashbox caisse = cashboxRepository.findFirstByBranchIdAndType(branchId, "vente")
                .orElseThrow(() -> new ResourceNotFoundException("Caisse vente introuvable pour cette branche"));

        if (caisse.getBalance().compareTo(dto.getAmount()) < 0) {
            throw new BusinessException("Le montant du dépôt ne peut pas être supérieur au montant de la caisse");
        }

        BankDeposit deposit = new BankDeposit();
        deposit.setBranchId(branchId);
        deposit.setBank(bank);
        deposit.setCashbox(caisse);
        deposit.setAmount(dto.getAmount());
        deposit.setDate(dto.getDate());
        deposit.setDescription(dto.getDescription());
        deposit.setAttachement(dto.getAttachement());
        BankDeposit saved = bankDepositRepository.save(deposit);

        CashboxOperation operation = new CashboxOperation();
        operation.setBranchId(branchId);
        operation.setCashbox(caisse);
        operation.setAmount(dto.getAmount());
        operation.setType("DEBIT");
        operation.setDescription("Dépôt bancaire vers " + bank.getName());
        operation.setOperationDate(dto.getDate());
        operation.setReference("DEP-" + saved.getId().toString().substring(0, 8).toUpperCase());
        cashboxOperationRepository.save(operation);

        caisse.setBalance(caisse.getBalance().subtract(dto.getAmount()));
        cashboxRepository.save(caisse);

        return toDepositDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BankDepositResponseDto> findDeposits(int page, int size, UUID branchId, UUID bankId, LocalDate date) {
        return PageResponse.of(bankDepositRepository.findWithFilters(branchId, bankId, date,
                PageRequest.of(page, size))
                .map(this::toDepositDto));
    }

    private BankResponseDto toDto(Bank b) {
        return new BankResponseDto(b.getId(), b.getName(), b.getAccountNumber(),
                b.getDescription(), b.getBranchId(), b.getCreatedAt());
    }

    private BankDepositResponseDto toDepositDto(BankDeposit d) {
        return new BankDepositResponseDto(
                d.getId(),
                d.getBank().getId(),
                d.getBank().getName(),
                d.getCashbox().getId(),
                d.getAmount(),
                d.getDate(),
                d.getDescription(),
                d.getAttachement(),
                d.getBranchId(),
                d.getCreatedAt()
        );
    }
}
