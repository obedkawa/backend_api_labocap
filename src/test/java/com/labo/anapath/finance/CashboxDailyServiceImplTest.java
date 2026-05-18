package com.labo.anapath.finance;

import com.labo.anapath.common.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CashboxDailyServiceImplTest {

    @Mock private CashboxDailyRepository cashboxDailyRepository;
    @Mock private CashboxRepository cashboxRepository;

    @InjectMocks private CashboxDailyServiceImpl service;

    private static final UUID BRANCH_ID  = UUID.randomUUID();
    private static final UUID USER_ID    = UUID.randomUUID();
    private static final UUID DAILY_ID   = UUID.randomUUID();

    private Cashbox buildCashbox(BigDecimal balance) {
        Cashbox c = new Cashbox();
        c.setBranchId(BRANCH_ID);
        c.setName("Caisse vente");
        c.setType("vente");
        c.setBalance(balance);
        c.setOpeningBalance(BigDecimal.ZERO);
        c.setStatut(0);
        return c;
    }

    @Test
    @DisplayName("openOrUpdate - journée existante → UPDATE sans créer de doublon")
    void openOrUpdate_existingEntry_updatesWithoutInsert() {
        Cashbox cashbox = buildCashbox(new BigDecimal("1000.00"));
        when(cashboxRepository.findFirstByBranchIdAndType(BRANCH_ID, "vente")).thenReturn(Optional.of(cashbox));

        CashboxDaily existing = new CashboxDaily();
        existing.setBranchId(BRANCH_ID);
        existing.setCashbox(cashbox);
        existing.setDate(LocalDate.now());
        existing.setStatus(0);
        existing.setOpeningBalance(BigDecimal.ZERO);
        existing.setClosingBalance(BigDecimal.ZERO);
        existing.setCode("OUV-20260518-XXX");

        when(cashboxDailyRepository.findByBranchIdAndCashboxIdAndDate(eq(BRANCH_ID), any(), eq(LocalDate.now())))
                .thenReturn(Optional.of(existing));
        when(cashboxDailyRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(cashboxRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CashboxDailyOpenDto dto = new CashboxDailyOpenDto();
        dto.setSoldeOuverture(new BigDecimal("200.00"));

        service.openOrUpdate(dto, BRANCH_ID, USER_ID);

        verify(cashboxDailyRepository, times(1)).save(any());
        assertThat(existing.getStatus()).isEqualTo(1);
        assertThat(existing.getOpeningBalance()).isEqualByComparingTo(new BigDecimal("200.00"));
    }

    @Test
    @DisplayName("openOrUpdate - nouvelle journée → INSERT et mise à jour solde caisse")
    void openOrUpdate_newEntry_createsAndUpdatesCashbox() {
        Cashbox cashbox = buildCashbox(new BigDecimal("1000.00"));
        when(cashboxRepository.findFirstByBranchIdAndType(BRANCH_ID, "vente")).thenReturn(Optional.of(cashbox));
        when(cashboxDailyRepository.findByBranchIdAndCashboxIdAndDate(any(), any(), any()))
                .thenReturn(Optional.empty());
        when(cashboxDailyRepository.save(any())).thenAnswer(inv -> {
            CashboxDaily d = inv.getArgument(0);
            if (d.getCode() == null) d.setCode("OUV-test");
            return d;
        });
        when(cashboxRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CashboxDailyOpenDto dto = new CashboxDailyOpenDto();
        dto.setSoldeOuverture(new BigDecimal("300.00"));

        service.openOrUpdate(dto, BRANCH_ID, USER_ID);

        ArgumentCaptor<Cashbox> cashboxCaptor = ArgumentCaptor.forClass(Cashbox.class);
        verify(cashboxRepository).save(cashboxCaptor.capture());
        assertThat(cashboxCaptor.getValue().getOpeningBalance()).isEqualByComparingTo(new BigDecimal("300.00"));
        assertThat(cashboxCaptor.getValue().getBalance()).isEqualByComparingTo(new BigDecimal("700.00")); // 1000 - 300
        assertThat(cashboxCaptor.getValue().getStatut()).isEqualTo(1);
    }

    @Test
    @DisplayName("closeCashbox - utilisateur non propriétaire → BusinessException")
    void closeCashbox_notOwner_throwsBusinessException() {
        CashboxDaily daily = new CashboxDaily();
        daily.setBranchId(BRANCH_ID);
        daily.setClosingBalance(BigDecimal.ZERO);
        // createdBy is a different user
        UUID otherUser = UUID.randomUUID();
        // AuditableEntity's createdBy is set by auditing, we simulate it via reflection or a different approach
        // We use the fact that getCreatedBy() returns null by default, and userId != null
        when(cashboxDailyRepository.findById(DAILY_ID)).thenReturn(Optional.of(daily));

        CashboxDailyCloseDto closeDto = new CashboxDailyCloseDto();

        // daily.getCreatedBy() is null, userId != null → throws
        assertThatThrownBy(() -> service.closeCashbox(DAILY_ID, closeDto, USER_ID))
                .isInstanceOf(BusinessException.class);
    }
}
