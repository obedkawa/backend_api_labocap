package com.labo.anapath.finance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CashboxOperationServiceImplTest {

    @Mock private CashboxOperationRepository cashboxOperationRepository;
    @Mock private CashboxRepository cashboxRepository;
    @InjectMocks private CashboxOperationServiceImpl service;

    private static final UUID BRANCH_ID  = UUID.randomUUID();
    private static final UUID CASHBOX_ID = UUID.randomUUID();

    private Cashbox buildCashbox(BigDecimal balance) {
        Cashbox c = new Cashbox();
        c.setBranchId(BRANCH_ID);
        c.setName("Test");
        c.setType("vente");
        c.setBalance(balance);
        return c;
    }

    @Test
    @DisplayName("create CREDIT → solde caisse augmente du montant")
    void create_credit_increasesCashboxBalance() {
        Cashbox cashbox = buildCashbox(new BigDecimal("1000.00"));
        when(cashboxRepository.findById(CASHBOX_ID)).thenReturn(Optional.of(cashbox));
        when(cashboxRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(cashboxOperationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CashboxOperationCreateDto dto = new CashboxOperationCreateDto();
        dto.setCashboxId(CASHBOX_ID);
        dto.setAmount(new BigDecimal("500.00"));
        dto.setType("CREDIT");

        service.create(dto, BRANCH_ID);

        assertThat(cashbox.getBalance()).isEqualByComparingTo(new BigDecimal("1500.00"));
    }

    @Test
    @DisplayName("create DEBIT → solde caisse diminue du montant")
    void create_debit_decreasesCashboxBalance() {
        Cashbox cashbox = buildCashbox(new BigDecimal("2000.00"));
        when(cashboxRepository.findById(CASHBOX_ID)).thenReturn(Optional.of(cashbox));
        when(cashboxRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(cashboxOperationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CashboxOperationCreateDto dto = new CashboxOperationCreateDto();
        dto.setCashboxId(CASHBOX_ID);
        dto.setAmount(new BigDecimal("300.00"));
        dto.setType("DEBIT");

        service.create(dto, BRANCH_ID);

        assertThat(cashbox.getBalance()).isEqualByComparingTo(new BigDecimal("1700.00"));
    }

    @Test
    @DisplayName("create - opération enregistrée avec branchId et cashbox corrects")
    void create_setsOperationFieldsCorrectly() {
        Cashbox cashbox = buildCashbox(BigDecimal.ZERO);
        when(cashboxRepository.findById(CASHBOX_ID)).thenReturn(Optional.of(cashbox));
        when(cashboxRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(cashboxOperationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CashboxOperationCreateDto dto = new CashboxOperationCreateDto();
        dto.setCashboxId(CASHBOX_ID);
        dto.setAmount(new BigDecimal("250.00"));
        dto.setType("CREDIT");
        dto.setDescription("Test paiement");

        service.create(dto, BRANCH_ID);

        ArgumentCaptor<CashboxOperation> opCaptor = ArgumentCaptor.forClass(CashboxOperation.class);
        verify(cashboxOperationRepository).save(opCaptor.capture());
        assertThat(opCaptor.getValue().getBranchId()).isEqualTo(BRANCH_ID);
        assertThat(opCaptor.getValue().getCashbox()).isSameAs(cashbox);
        assertThat(opCaptor.getValue().getDescription()).isEqualTo("Test paiement");
    }
}
