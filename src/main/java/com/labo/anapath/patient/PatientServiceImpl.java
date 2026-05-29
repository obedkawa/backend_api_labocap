package com.labo.anapath.patient;

import com.labo.anapath.common.dto.PageResponse;
import com.labo.anapath.common.exception.BusinessException;
import com.labo.anapath.common.exception.DuplicateResourceException;
import com.labo.anapath.common.exception.ResourceNotFoundException;
import com.labo.anapath.finance.Invoice;
import com.labo.anapath.finance.InvoiceRepository;
import com.labo.anapath.finance.InvoiceStatus;
import com.labo.anapath.testorder.TestOrder;
import com.labo.anapath.testorder.TestOrderRepository;
import com.labo.anapath.testorder.TestOrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Implémentation de {@link PatientService} portant la logique métier des dossiers patients.
 * <p>
 * Les règles métier appliquées sont :
 * <ul>
 *   <li>Unicité du code patient au sein d'une agence (création et mise à jour)</li>
 *   <li>Unicité du numéro de téléphone principal au sein d'une agence</li>
 *   <li>Interdiction de suppression si le patient a des demandes d'examen</li>
 * </ul>
 * La méthode {@link #getProfile(UUID)} agrège en mémoire les données des TestOrder
 * et des Invoice pour éviter des requêtes JPQL complexes et maintenir la lisibilité.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;
    // Utilisé pour vérifier les dépendances avant suppression et construire le profil
    private final TestOrderRepository testOrderRepository;
    // Utilisé pour construire le résumé financier du profil patient
    private final InvoiceRepository invoiceRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PatientResponseDto> findAll(int page, int size, String search, UUID branchId) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<PatientResponseDto> result;
        // Basculer sur la recherche multi-champs uniquement si un terme est fourni
        if (StringUtils.hasText(search)) {
            result = patientRepository.findByBranchIdAndSearchTerm(branchId, search, pageRequest)
                    .map(patientMapper::toResponseDto);
        } else {
            result = patientRepository.findByBranchId(branchId, pageRequest)
                    .map(patientMapper::toResponseDto);
        }
        return PageResponse.of(result);
    }

    @Override
    @Transactional(readOnly = true)
    public PatientResponseDto findById(UUID id, UUID branchId) {
        Patient patient = patientRepository.findByIdAndBranchId(id, branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", id));
        return patientMapper.toResponseDto(patient);
    }

    @Override
    @Transactional
    public PatientResponseDto create(PatientRequestDto dto, UUID branchId) {
        if (StringUtils.hasText(dto.getCode())
                && patientRepository.existsByCodeAndBranchId(dto.getCode(), branchId)) {
            throw new DuplicateResourceException(
                    "Un patient avec le code '" + dto.getCode() + "' existe déjà dans cette agence.");
        }
        if (StringUtils.hasText(dto.getTelephone1())
                && patientRepository.existsByTelephone1AndBranchId(dto.getTelephone1(), branchId)) {
            throw new DuplicateResourceException(
                    "Un patient avec le téléphone '" + dto.getTelephone1() + "' existe déjà dans cette agence.");
        }
        Patient patient = patientMapper.toEntity(dto);
        patient.setBranchId(branchId);
        Patient saved = patientRepository.save(patient);
        log.info("Patient créé: {}", saved.getId());
        return patientMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    public PatientResponseDto update(UUID id, PatientRequestDto dto, UUID branchId) {
        Patient patient = patientRepository.findByIdAndBranchId(id, branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", id));
        // Vérifier le code uniquement s'il change réellement
        if (StringUtils.hasText(dto.getCode())
                && !dto.getCode().equals(patient.getCode())
                && patientRepository.existsByCodeAndBranchIdAndIdNot(dto.getCode(), branchId, id)) {
            throw new DuplicateResourceException(
                    "Un patient avec le code '" + dto.getCode() + "' existe déjà.");
        }
        // Vérifier le téléphone uniquement s'il change réellement
        if (StringUtils.hasText(dto.getTelephone1())
                && !dto.getTelephone1().equals(patient.getTelephone1())
                && patientRepository.existsByTelephone1AndBranchId(dto.getTelephone1(), branchId)) {
            throw new DuplicateResourceException(
                    "Un patient avec le téléphone '" + dto.getTelephone1() + "' existe déjà dans cette agence.");
        }
        patientMapper.updateEntityFromDto(dto, patient);
        return patientMapper.toResponseDto(patientRepository.save(patient));
    }

    @Override
    @Transactional
    public void delete(UUID id, UUID branchId) {
        Patient patient = patientRepository.findByIdAndBranchId(id, branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", id));
        // Interdire la suppression si des demandes d'examen référencent ce patient
        if (testOrderRepository.existsByPatient(patient)) {
            throw new BusinessException("PATIENT_HAS_ORDERS");
        }
        patientRepository.delete(patient);
        log.info("Patient supprimé (soft): {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public PatientProfileDto getProfile(UUID id, UUID branchId) {
        Patient patient = patientRepository.findByIdAndBranchId(id, branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", id));

        List<TestOrder> orders = testOrderRepository.findByPatientOrderByCreatedAtDesc(patient);
        List<Invoice> invoices = invoiceRepository.findByPatientOrderByCreatedAtDesc(patient);

        int totalOrders = orders.size();
        // Compter les demandes en attente de traitement
        int pendingOrders = (int) orders.stream()
                .filter(o -> o.getStatus() == TestOrderStatus.PENDING).count();
        // Compter les demandes validées ou livrées (considérées comme terminées)
        int completedOrders = (int) orders.stream()
                .filter(o -> o.getStatus() == TestOrderStatus.VALIDATED
                          || o.getStatus() == TestOrderStatus.DELIVERED).count();

        List<TestOrderSummaryDto> orderSummaries = orders.stream()
                .map(o -> new TestOrderSummaryDto(
                        o.getId(), o.getCode(), o.getStatus().name(),
                        o.getPrelevementDate(), o.getCreatedAt()))
                .toList();

        // Calculer les totaux financiers en agrégeant toutes les factures
        BigDecimal totalInvoiced = invoices.stream()
                .map(Invoice::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalPaid = invoices.stream()
                .filter(i -> i.getStatus() == InvoiceStatus.PAID)
                .map(Invoice::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalUnpaid = totalInvoiced.subtract(totalPaid);

        List<InvoiceSummaryDto> invoiceSummaries = invoices.stream()
                .map(i -> new InvoiceSummaryDto(
                        i.getId(), i.getTotal(), i.getStatus().name(),
                        i.getDueDate(), i.getCreatedAt()))
                .toList();

        return new PatientProfileDto(
                patientMapper.toResponseDto(patient),
                totalOrders, pendingOrders, completedOrders,
                orderSummaries,
                totalInvoiced, totalPaid, totalUnpaid,
                invoiceSummaries
        );
    }
}
