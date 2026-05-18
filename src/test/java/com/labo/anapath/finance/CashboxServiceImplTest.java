package com.labo.anapath.finance;

import com.labo.anapath.common.exception.ResourceNotFoundException;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CashboxServiceImplTest {

    @Mock private CashboxRepository cashboxRepository;
    @InjectMocks private CashboxServiceImpl cashboxService;

    private static final UUID BRANCH_ID  = UUID.randomUUID();
    private static final UUID CASHBOX_ID = UUID.randomUUID();

    @Test
    @DisplayName("create - caisse créée avec solde initial 0")
    void create_setsInitialBalanceToZero() {
        CashboxRequestDto dto = new CashboxRequestDto();
        dto.setName("Caisse principale");
        dto.setType("vente");
        when(cashboxRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        cashboxService.create(dto, BRANCH_ID);

        ArgumentCaptor<Cashbox> captor = ArgumentCaptor.forClass(Cashbox.class);
        verify(cashboxRepository).save(captor.capture());
        assertThat(captor.getValue().getBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(captor.getValue().getType()).isEqualTo("vente");
        assertThat(captor.getValue().getName()).isEqualTo("Caisse principale");
    }

    @Test
    @DisplayName("findById - caisse introuvable → ResourceNotFoundException")
    void findById_notFound_throws404() {
        when(cashboxRepository.findById(CASHBOX_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cashboxService.findById(CASHBOX_ID))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("update - name et type mis à jour")
    void update_updatesNameAndType() {
        Cashbox cashbox = new Cashbox();
        cashbox.setName("Ancienne");
        cashbox.setType("vente");
        when(cashboxRepository.findById(CASHBOX_ID)).thenReturn(Optional.of(cashbox));
        when(cashboxRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CashboxRequestDto dto = new CashboxRequestDto();
        dto.setName("Nouvelle caisse");
        dto.setType("depense");
        cashboxService.update(CASHBOX_ID, dto);

        assertThat(cashbox.getName()).isEqualTo("Nouvelle caisse");
        assertThat(cashbox.getType()).isEqualTo("depense");
    }

    @Test
    @DisplayName("delete - soft delete via repository.delete()")
    void delete_callsRepositoryDelete() {
        Cashbox cashbox = new Cashbox();
        when(cashboxRepository.findById(CASHBOX_ID)).thenReturn(Optional.of(cashbox));

        cashboxService.delete(CASHBOX_ID);

        verify(cashboxRepository).delete(cashbox);
    }
}
