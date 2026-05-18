package com.labo.anapath.finance;

import com.labo.anapath.common.dto.PageResponse;
import com.labo.anapath.common.exception.InvalidOperationException;
import com.labo.anapath.common.exception.ResourceNotFoundException;
import com.labo.anapath.contract.ContratRepository;
import com.labo.anapath.patient.PatientRepository;
import com.labo.anapath.test.LabTestRepository;
import com.labo.anapath.testorder.TestOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final TestOrderRepository testOrderRepository;
    private final PatientRepository patientRepository;
    private final LabTestRepository labTestRepository;
    private final CashboxRepository cashboxRepository;
    private final ContratRepository contratRepository;
    private final FinanceMapper financeMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<InvoiceResponseDto> findAll(int page, int size, UUID branchId) {
        return PageResponse.of(invoiceRepository.findByBranchId(branchId,
                PageRequest.of(page, size, Sort.by("createdAt").descending()))
                .map(financeMapper::toInvoiceResponseDto));
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceResponseDto findById(UUID id) {
        return financeMapper.toInvoiceResponseDto(invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facture", id)));
    }

    @Override
    @Transactional
    public InvoiceResponseDto create(InvoiceRequestDto dto, UUID branchId) {
        Invoice invoice = new Invoice();
        invoice.setBranchId(branchId);
        invoice.setDueDate(dto.getDueDate());
        invoice.setStatus(InvoiceStatus.PENDING);

        invoice.setTestOrder(testOrderRepository.findById(dto.getTestOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Bon d'examen", dto.getTestOrderId())));
        invoice.setPatient(patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient", dto.getPatientId())));

        BigDecimal total = BigDecimal.ZERO;
        List<InvoiceDetail> details = new ArrayList<>();
        for (InvoiceRequestDto.InvoiceDetailRequestDto detailDto : dto.getDetails()) {
            InvoiceDetail detail = new InvoiceDetail();
            detail.setInvoice(invoice);
            detail.setQuantity(detailDto.getQuantity());
            detail.setUnitPrice(detailDto.getUnitPrice());
            detail.setTotal(detailDto.getUnitPrice().multiply(BigDecimal.valueOf(detailDto.getQuantity())));
            detail.setLabTest(labTestRepository.findById(detailDto.getLabTestId())
                    .orElseThrow(() -> new ResourceNotFoundException("Analyse", detailDto.getLabTestId())));
            total = total.add(detail.getTotal());
            details.add(detail);
        }
        invoice.setTotal(total);
        invoice.setDetails(details);

        return financeMapper.toInvoiceResponseDto(invoiceRepository.save(invoice));
    }

    @Override
    @Transactional
    public InvoiceResponseDto update(UUID id, InvoiceRequestDto dto) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facture", id));
        invoice.setDueDate(dto.getDueDate());
        return financeMapper.toInvoiceResponseDto(invoiceRepository.save(invoice));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facture", id));
        invoiceRepository.delete(invoice);
    }

    @Override
    @Transactional
    public InvoiceResponseDto markAsPaid(UUID invoiceId, InvoiceStatusUpdateDto dto) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Facture", invoiceId));

        if (Boolean.TRUE.equals(invoice.getPaid())) {
            throw new InvalidOperationException("INVOICE_ALREADY_PAID");
        }

        invoice.setPaid(true);
        invoice.setPayment(dto.getPayment());
        invoice.setStatus(InvoiceStatus.PAID);

        // R2 — JAMAIS par ID hardcodé : utiliser findByBranchIdAndType
        if (invoice.getStatusInvoice() == 0) {
            // Facture de vente → crédit caisse vente
            Cashbox cashVente = cashboxRepository.findFirstByBranchIdAndType(invoice.getBranchId(), "vente")
                    .orElseThrow(() -> new ResourceNotFoundException("Caisse vente", null));
            cashVente.setBalance(cashVente.getBalance().add(invoice.getTotal()));
            cashboxRepository.save(cashVente);
        } else {
            // Avoir → débit caisse dépense
            Cashbox cashDepense = cashboxRepository.findFirstByBranchIdAndType(invoice.getBranchId(), "depense")
                    .orElseThrow(() -> new ResourceNotFoundException("Caisse dépense", null));
            cashDepense.setBalance(cashDepense.getBalance().subtract(invoice.getTotal()));
            cashboxRepository.save(cashDepense);
        }

        // R4 — Auto-clôture contrat si invoice_unique
        if (invoice.getContrat() != null && Boolean.TRUE.equals(invoice.getContrat().getInvoiceUnique())) {
            invoice.getContrat().setIsClose(true);
            contratRepository.save(invoice.getContrat());
        }

        return financeMapper.toInvoiceResponseDto(invoiceRepository.save(invoice));
    }

    @Override
    @Transactional(readOnly = true)
    public BusinessDashboardDto getBusinessDashboard(UUID branchId) {
        LocalDate today = LocalDate.now();
        int currentMonth = today.getMonthValue();
        int currentYear = today.getYear();
        int lastMonth = today.minusMonths(1).getMonthValue();
        int lastYear = today.minusMonths(1).getYear();

        BigDecimal totalToday = invoiceRepository.sumPaidByBranchIdAndDate(branchId, today);
        BigDecimal totalMonth = invoiceRepository.sumPaidByBranchIdAndMonth(branchId, currentMonth, currentYear);
        BigDecimal totalLastMonth = invoiceRepository.sumPaidByBranchIdAndMonth(branchId, lastMonth, lastYear);

        return new BusinessDashboardDto(totalToday, totalMonth, totalLastMonth);
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceSearchResultDto searchByPeriod(LocalDate startDate, LocalDate endDate, UUID branchId) {
        BigDecimal ca = invoiceRepository.sumVenteByBranchIdAndDateRange(branchId, startDate, endDate);
        BigDecimal avoir = invoiceRepository.sumAvoirByBranchIdAndDateRange(branchId, startDate, endDate);
        BigDecimal facture = invoiceRepository.sumTotalByBranchIdAndDateRange(branchId, startDate, endDate);
        BigDecimal encaissement = ca.subtract(avoir);
        return new InvoiceSearchResultDto(ca, avoir, facture, encaissement);
    }
}
