package com.labo.anapath.finance;

import com.labo.anapath.inventory.Article;
import com.labo.anapath.inventory.ArticleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
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
class CashboxVoucherServiceImplTest {

    @Mock private CashboxVoucherRepository voucherRepository;
    @Mock private CashboxVoucherDetailRepository detailRepository;
    @Mock private ExpenseRepository expenseRepository;
    @Mock private ExpenceDetailRepository expenceDetailRepository;
    @Mock private ArticleRepository articleRepository;

    @InjectMocks private CashboxVoucherServiceImpl service;

    private static final UUID BRANCH_ID  = UUID.randomUUID();
    private static final UUID VOUCHER_ID = UUID.randomUUID();
    private static final UUID USER_ID    = UUID.randomUUID();

    private CashboxVoucher buildVoucher(String status, BigDecimal amount) {
        CashboxVoucher v = new CashboxVoucher();
        v.setBranchId(BRANCH_ID);
        v.setDescription("Test bon");
        v.setStatus(status);
        v.setAmount(amount);
        return v;
    }

    @Test
    @DisplayName("updateStatus 'approuve' → crée Expense et copie les ExpenceDetail")
    void updateStatus_approuve_createsExpenseAndCopiesDetails() {
        CashboxVoucher voucher = buildVoucher("en attente", new BigDecimal("500.00"));
        when(voucherRepository.findById(VOUCHER_ID)).thenReturn(Optional.of(voucher));
        when(voucherRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CashboxVoucherDetail detail = new CashboxVoucherDetail();
        detail.setBranchId(BRANCH_ID);
        detail.setItemName("Gants");
        detail.setQuantity(new BigDecimal("10"));
        detail.setUnitPrice(new BigDecimal("50.00"));
        detail.setLineAmount(new BigDecimal("500.00"));

        when(detailRepository.findByCashboxVoucherId(any())).thenReturn(List.of(detail));
        when(expenseRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(articleRepository.findFirstByBranchIdAndName(BRANCH_ID, "Gants")).thenReturn(Optional.empty());

        CashboxVoucherStatusDto dto = new CashboxVoucherStatusDto();
        dto.setStatus("approuve");

        service.updateStatus(VOUCHER_ID, dto, BRANCH_ID, USER_ID);

        ArgumentCaptor<Expense> expenseCaptor = ArgumentCaptor.forClass(Expense.class);
        verify(expenseRepository).save(expenseCaptor.capture());
        assertThat(expenseCaptor.getValue().getAmount()).isEqualByComparingTo(new BigDecimal("500.00"));
        assertThat(expenseCaptor.getValue().getPaid()).isEqualTo(0);

        ArgumentCaptor<ExpenceDetail> detailCaptor = ArgumentCaptor.forClass(ExpenceDetail.class);
        verify(expenceDetailRepository).save(detailCaptor.capture());
        assertThat(detailCaptor.getValue().getArticleName()).isEqualTo("Gants");
        assertThat(detailCaptor.getValue().getLineAmount()).isEqualByComparingTo(new BigDecimal("500.00"));
    }

    @Test
    @DisplayName("updateStatus 'approuve' → article trouvé → articleId renseigné dans ExpenceDetail")
    void updateStatus_approuve_articleFound_setsArticleId() {
        CashboxVoucher voucher = buildVoucher("en attente", new BigDecimal("200.00"));
        when(voucherRepository.findById(VOUCHER_ID)).thenReturn(Optional.of(voucher));
        when(voucherRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CashboxVoucherDetail detail = new CashboxVoucherDetail();
        detail.setBranchId(BRANCH_ID);
        detail.setItemName("Seringues");
        detail.setQuantity(new BigDecimal("20"));
        detail.setUnitPrice(new BigDecimal("10.00"));
        detail.setLineAmount(new BigDecimal("200.00"));

        UUID articleId = UUID.randomUUID();
        Article article = new Article();
        article.setBranchId(BRANCH_ID);

        when(detailRepository.findByCashboxVoucherId(any())).thenReturn(List.of(detail));
        when(expenseRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(articleRepository.findFirstByBranchIdAndName(BRANCH_ID, "Seringues")).thenReturn(Optional.of(article));

        CashboxVoucherStatusDto dto = new CashboxVoucherStatusDto();
        dto.setStatus("approuve");

        service.updateStatus(VOUCHER_ID, dto, BRANCH_ID, USER_ID);

        ArgumentCaptor<ExpenceDetail> detailCaptor = ArgumentCaptor.forClass(ExpenceDetail.class);
        verify(expenceDetailRepository).save(detailCaptor.capture());
        assertThat(detailCaptor.getValue().getArticleId()).isEqualTo(article.getId());
    }

    @Test
    @DisplayName("updateStatus statut invalide → BusinessException")
    void updateStatus_invalidStatus_throwsBusinessException() {
        CashboxVoucher voucher = buildVoucher("en attente", BigDecimal.ZERO);
        when(voucherRepository.findById(VOUCHER_ID)).thenReturn(Optional.of(voucher));

        CashboxVoucherStatusDto dto = new CashboxVoucherStatusDto();
        dto.setStatus("INVALID");

        assertThatThrownBy(() -> service.updateStatus(VOUCHER_ID, dto, BRANCH_ID, USER_ID))
                .isInstanceOf(com.labo.anapath.common.exception.BusinessException.class);
    }

    @Test
    @DisplayName("updateStatus 'rejete' → change le statut sans créer de dépense")
    void updateStatus_rejete_changesStatusWithoutCreatingExpense() {
        CashboxVoucher voucher = buildVoucher("en attente", new BigDecimal("300.00"));
        when(voucherRepository.findById(VOUCHER_ID)).thenReturn(Optional.of(voucher));
        when(voucherRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CashboxVoucherStatusDto dto = new CashboxVoucherStatusDto();
        dto.setStatus("rejete");

        CashboxVoucherResponseDto result = service.updateStatus(VOUCHER_ID, dto, BRANCH_ID, USER_ID);

        assertThat(result.status()).isEqualTo("rejete");

        // Aucune dépense ne doit être créée lors d'un rejet
        org.mockito.Mockito.verifyNoInteractions(expenseRepository);
        org.mockito.Mockito.verifyNoInteractions(expenceDetailRepository);
    }

    @Test
    @DisplayName("addDetail → met à jour le montant total du bon")
    void addDetail_updatesVoucherAmount() {
        CashboxVoucher voucher = buildVoucher("en attente", new BigDecimal("100.00"));
        when(voucherRepository.findById(VOUCHER_ID)).thenReturn(Optional.of(voucher));
        when(detailRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(voucherRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(voucherRepository.findById(VOUCHER_ID)).thenReturn(Optional.of(voucher));

        // simulate lazy load
        voucher.getDetails().clear();

        CashboxVoucherDetailRequestDto dto = new CashboxVoucherDetailRequestDto();
        dto.setItemName("Gants stériles");
        dto.setQuantity(new BigDecimal("5"));
        dto.setUnitPrice(new BigDecimal("40.00"));

        service.addDetail(VOUCHER_ID, dto, BRANCH_ID);

        assertThat(voucher.getAmount()).isEqualByComparingTo(new BigDecimal("300.00")); // 100 + 200
    }
}
