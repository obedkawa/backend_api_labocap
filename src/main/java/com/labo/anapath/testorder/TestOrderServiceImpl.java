package com.labo.anapath.testorder;

import com.labo.anapath.common.dto.PageResponse;
import com.labo.anapath.common.exception.BusinessException;
import com.labo.anapath.common.exception.DuplicateResourceException;
import com.labo.anapath.common.exception.InvalidOperationException;
import com.labo.anapath.common.exception.ResourceNotFoundException;
import com.labo.anapath.contract.ContratRepository;
import com.labo.anapath.doctor.DoctorRepository;
import com.labo.anapath.doctor.HospitalRepository;
import com.labo.anapath.finance.Invoice;
import com.labo.anapath.finance.InvoiceDetail;
import com.labo.anapath.finance.InvoiceDetailRepository;
import com.labo.anapath.finance.InvoiceRepository;
import com.labo.anapath.patient.PatientRepository;
import com.labo.anapath.report.LogReport;
import com.labo.anapath.report.LogReportRepository;
import com.labo.anapath.report.Report;
import com.labo.anapath.report.ReportRepository;
import com.labo.anapath.report.ReportStatus;
import com.labo.anapath.setting.SettingRepository;
import com.labo.anapath.test.LabTest;
import com.labo.anapath.test.LabTestRepository;
import com.labo.anapath.test.TypeOrderRepository;
import com.labo.anapath.user.User;
import com.labo.anapath.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Implémentation du service des bons d'examen anatomopathologiques.
 *
 * <p>Orchestre le workflow central du LIS : création des bons, validation (avec génération
 * de code unique, création du compte-rendu DRAFT, journalisation et facturation),
 * mise à jour, suppression et livraison.
 *
 * <p>La protection contre les race conditions lors de la génération du code est assurée
 * par l'interception de {@link org.springframework.dao.DataIntegrityViolationException}
 * sur la contrainte d'unicité du champ {@code code}.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TestOrderServiceImpl implements TestOrderService {

    private final TestOrderRepository testOrderRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final HospitalRepository hospitalRepository;
    private final ContratRepository contratRepository;
    private final TypeOrderRepository typeOrderRepository;
    private final LabTestRepository labTestRepository;
    private final TestOrderMapper testOrderMapper;
    private final ReportRepository reportRepository;
    private final LogReportRepository logReportRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceDetailRepository invoiceDetailRepository;
    private final UserRepository userRepository;
    private final SettingRepository settingRepository;
    private final FileStorageService fileStorageService;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<TestOrderResponseDto> findAll(int page, int size, TestOrderFilterDto filter, UUID branchId) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<TestOrderResponseDto> result = testOrderRepository
                .findAll(TestOrderSpecification.filter(branchId, filter), pageRequest)
                .map(testOrderMapper::toResponseDto);
        return PageResponse.of(result);
    }

    @Override
    @Transactional(readOnly = true)
    public TestOrderResponseDto findById(UUID id, UUID branchId) {
        return testOrderMapper.toResponseDto(testOrderRepository.findByIdAndBranchId(id, branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Bon d'examen", id)));
    }

    /**
     * Crée un nouveau bon d'examen au statut PENDING.
     *
     * <p>Calcule automatiquement le total de chaque ligne d'analyse :
     * {@code total = prix - (prix × remise / 100)}.
     *
     * @param dto      données saisies par l'utilisateur
     * @param branchId branche du technicien connecté
     * @return le bon persisté avec ses détails
     */
    @Override
    @Transactional
    public TestOrderResponseDto create(TestOrderRequestDto dto, UUID branchId) {
        TestOrder order = new TestOrder();
        order.setBranchId(branchId);
        order.setCode(null);
        order.setStatus(TestOrderStatus.PENDING);
        order.setPrelevementDate(dto.getPrelevementDate());
        order.setReferenceHopital(dto.getReferenceHopital());
        order.setIsUrgent(dto.getIsUrgent() != null ? dto.getIsUrgent() : false);
        order.setOption(dto.getOption());
        order.setTestAffiliate(dto.getTestAffiliate());
        order.setSubtotal(dto.getSubtotal());
        order.setDiscount(dto.getDiscount());
        order.setTotal(dto.getTotal());

        order.setPatient(patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient", dto.getPatientId())));

        if (dto.getDoctorId() != null) {
            order.setDoctor(doctorRepository.findById(dto.getDoctorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Médecin", dto.getDoctorId())));
        }
        if (dto.getHospitalId() != null) {
            order.setHospital(hospitalRepository.findById(dto.getHospitalId())
                    .orElseThrow(() -> new ResourceNotFoundException("Hôpital", dto.getHospitalId())));
        }
        if (dto.getContratId() != null) {
            order.setContrat(contratRepository.findById(dto.getContratId())
                    .orElseThrow(() -> new ResourceNotFoundException("Contrat", dto.getContratId())));
        }
        if (dto.getTypeOrderId() != null) {
            order.setTypeOrder(typeOrderRepository.findById(dto.getTypeOrderId())
                    .orElseThrow(() -> new ResourceNotFoundException("Type de bon", dto.getTypeOrderId())));
        }

        if (dto.getDetails() != null && !dto.getDetails().isEmpty()) {
            List<DetailTestOrder> details = dto.getDetails().stream().map(detailDto -> {
                LabTest labTest = labTestRepository.findById(detailDto.getLabTestId())
                        .orElseThrow(() -> new ResourceNotFoundException("Analyse", detailDto.getLabTestId()));
                DetailTestOrder detail = new DetailTestOrder();
                detail.setTestOrder(order);
                detail.setLabTest(labTest);
                detail.setTestName(labTest.getName() != null ? labTest.getName() : "");
                double price = labTest.getPrice() != null ? labTest.getPrice().doubleValue() : 0.0;
                double disc = detailDto.getDiscount() != null ? detailDto.getDiscount() : 0.0;
                detail.setPrice(price);
                detail.setDiscount(disc);
                detail.setTotal(price - (price * disc / 100));
                return detail;
            }).toList();
            order.getDetails().addAll(details);
        }

        TestOrder saved = testOrderRepository.save(order);
        log.info("Bon d'examen créé: id={}, patient={}", saved.getId(), saved.getPatient().getId());
        return testOrderMapper.toResponseDto(saved);
    }

    /**
     * Met à jour un bon d'examen existant. Interdit sur un bon déjà validé.
     *
     * @param id       identifiant UUID du bon
     * @param dto      nouvelles données
     * @param branchId identifiant de la branche (isolation multi-tenant)
     * @return le bon mis à jour
     */
    @Override
    @Transactional
    public TestOrderResponseDto update(UUID id, TestOrderRequestDto dto, UUID branchId) {
        TestOrder order = testOrderRepository.findByIdAndBranchId(id, branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Bon d'examen", id));
        if (order.getStatus() == TestOrderStatus.VALIDATED) {
            throw new InvalidOperationException("Impossible de modifier un bon d'examen déjà validé.");
        }
        order.setPrelevementDate(dto.getPrelevementDate());
        order.setReferenceHopital(dto.getReferenceHopital());
        if (dto.getIsUrgent() != null) order.setIsUrgent(dto.getIsUrgent());
        if (dto.getOption() != null) order.setOption(dto.getOption());
        if (dto.getTestAffiliate() != null) order.setTestAffiliate(dto.getTestAffiliate());

        if (dto.getDoctorId() != null) {
            order.setDoctor(doctorRepository.findById(dto.getDoctorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Médecin", dto.getDoctorId())));
        }
        if (dto.getHospitalId() != null) {
            order.setHospital(hospitalRepository.findById(dto.getHospitalId())
                    .orElseThrow(() -> new ResourceNotFoundException("Hôpital", dto.getHospitalId())));
        }
        if (dto.getContratId() != null) {
            order.setContrat(contratRepository.findById(dto.getContratId())
                    .orElseThrow(() -> new ResourceNotFoundException("Contrat", dto.getContratId())));
        }
        if (dto.getTypeOrderId() != null) {
            order.setTypeOrder(typeOrderRepository.findById(dto.getTypeOrderId())
                    .orElseThrow(() -> new ResourceNotFoundException("Type de bon", dto.getTypeOrderId())));
        }
        return testOrderMapper.toResponseDto(testOrderRepository.save(order));
    }

    /**
     * Supprime (soft delete) un bon d'examen non encore validé.
     * Un bon validé ne peut pas être supprimé pour préserver la traçabilité.
     *
     * @param id       identifiant UUID du bon
     * @param branchId identifiant de la branche (isolation multi-tenant)
     */
    @Override
    @Transactional
    public void delete(UUID id, UUID branchId) {
        TestOrder order = testOrderRepository.findByIdAndBranchId(id, branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Bon d'examen", id));
        if (order.getStatus() == TestOrderStatus.VALIDATED) {
            throw new InvalidOperationException("Impossible de supprimer un bon d'examen déjà validé.");
        }
        testOrderRepository.delete(order);
        log.info("Bon d'examen supprimé (soft): {}", id);
    }

    /**
     * Déclenche le workflow de validation du bon d'examen (passage au statut VALIDATED).
     *
     * <p>Séquence complète :
     * <ol>
     *   <li>Seul le statut {@code VALIDATED} est accepté par cet endpoint</li>
     *   <li>Récupération du bon filtrée par branchId (sécurité multi-tenant)</li>
     *   <li>Vérification de la présence du contrat (obligatoire)</li>
     *   <li>Génération du code unique si le bon n'en possède pas encore ;
     *       une {@link org.springframework.dao.DataIntegrityViolationException} est traduite
     *       en HTTP 409 pour protéger contre les race conditions concurrentes</li>
     *   <li>Création ou mise à jour du {@link Report} associé (code = "CO" + code du bon)</li>
     *   <li>Journalisation de l'action dans {@link LogReport}</li>
     *   <li>Facturation : individuelle si {@code invoice_unique=false}, groupée sinon</li>
     * </ol>
     *
     * @param id       identifiant UUID du bon
     * @param status   valeur attendue : {@code "VALIDATED"}
     * @param userId   identifiant de l'utilisateur effectuant la validation (pour le log)
     * @param branchId identifiant de la branche (isolation multi-tenant)
     * @return le bon validé avec son code généré
     */
    @Override
    @Transactional
    public TestOrderResponseDto updateStatus(UUID id, String status, UUID userId, UUID branchId) {
        // AC3: seul VALIDATED est supporté via cet endpoint
        if (!"VALIDATED".equalsIgnoreCase(status)) {
            throw new BusinessException("Seul le statut VALIDATED est géré par cet endpoint");
        }

        // AC4: bon doit exister, filtré par branchId (sécurité multi-tenant)
        TestOrder order = testOrderRepository.findByIdAndBranchId(id, branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Bon d'examen", id));

        // AC10: contrat obligatoire
        if (order.getContrat() == null) {
            throw new BusinessException("TEST_ORDER_NO_CONTRACT");
        }

        // AC1/AC2: génération code uniquement si pas encore validé
        if (order.getCode() == null) {
            String code = generateCodeExamen(branchId);
            order.setCode(code);
            order.setStatus(TestOrderStatus.VALIDATED);
            try {
                testOrderRepository.saveAndFlush(order);
            } catch (DataIntegrityViolationException e) {
                // AC9: race condition — collision sur le code unique → HTTP 409
                log.warn("Race condition lors de la validation du bon {}: code={}", id, code);
                throw new DuplicateResourceException("CODE_GENERATION_CONFLICT");
            }
        }

        // AC5: Report — création ou mise à jour du code
        Report report = reportRepository.findByTestOrderId(order.getId()).orElse(null);
        if (report == null) {
            report = new Report();
            report.setBranchId(branchId);
            report.setTestOrder(order);
            report.setStatus(ReportStatus.DRAFT);
            String placeholder = settingRepository
                    .findByKeyAndBranchId("prefixe_code_demande_examen", branchId)
                    .map(s -> s.getPlaceholder() != null ? s.getPlaceholder() : "")
                    .orElse("");
            report.setDescription(placeholder);
        }
        report.setCode("CO" + order.getCode());
        report = reportRepository.save(report);

        // AC6: LogReport
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", userId));
        LogReport logReport = new LogReport();
        logReport.setBranchId(branchId);
        logReport.setReport(report);
        logReport.setUser(user);
        logReport.setAction("Créer un nouveau report");
        logReportRepository.save(logReport);
        log.info("Report créé/mis à jour pour bon {}: code={}", id, report.getCode());

        // AC7/AC8: Facturation
        boolean invoiceGrouped = Boolean.TRUE.equals(order.getContrat().getInvoiceUnique());
        if (!invoiceGrouped) {
            processIndividualInvoice(order, branchId);
        } else {
            processGroupedInvoice(order);
        }

        return testOrderMapper.toResponseDto(
                testOrderRepository.findById(id).orElseThrow());
    }

    /**
     * Traite la facturation individuelle pour un contrat avec {@code invoice_unique=false}.
     *
     * <p>Crée une nouvelle facture dédiée à ce bon d'examen si elle n'existe pas encore,
     * sinon met à jour les montants de la facture existante.
     * Dans tous les cas, ajoute les lignes de détail non encore facturées.
     *
     * @param order    le bon d'examen à facturer
     * @param branchId identifiant de la branche (nécessaire pour la création de la facture)
     */
    // AC7: contrat individuel (invoice_unique=false)
    private void processIndividualInvoice(TestOrder order, UUID branchId) {
        Invoice invoice = invoiceRepository.findByTestOrderId(order.getId()).orElse(null);
        if (invoice == null) {
            invoice = new Invoice();
            invoice.setBranchId(branchId);
            invoice.setTestOrder(order);
            invoice.setPatient(order.getPatient());
            invoice.setContrat(order.getContrat());
            invoice.setClientName(
                    order.getPatient().getFirstname() + " " + order.getPatient().getLastname());
            invoice.setSubtotal(order.getSubtotal());
            invoice.setDiscount(order.getDiscount());
            invoice.setTotal(order.getTotal() != null
                    ? BigDecimal.valueOf(order.getTotal()) : BigDecimal.ZERO);
            invoice.setCode(generateCodeFacture(branchId));
            invoice = invoiceRepository.save(invoice);
        } else {
            invoice.setSubtotal(order.getSubtotal());
            invoice.setDiscount(order.getDiscount());
            invoice.setTotal(order.getTotal() != null
                    ? BigDecimal.valueOf(order.getTotal()) : BigDecimal.ZERO);
            invoiceRepository.save(invoice);
        }
        addInvoiceDetails(invoice, order.getDetails());
    }

    /**
     * Traite la facturation groupée pour un contrat avec {@code invoice_unique=true}.
     *
     * <p>Récupère la facture ouverte la plus récente du contrat et cumule les montants du bon.
     * Lève une {@link com.labo.anapath.common.exception.BusinessException} si aucune facture
     * ouverte n'existe ({@code CONTRACT_NO_INVOICE}) ou si elle est déjà payée
     * ({@code CONTRACT_INVOICE_ALREADY_PAID}).
     *
     * @param order le bon d'examen dont les montants sont à cumuler sur la facture groupée
     */
    // AC8: contrat groupé (invoice_unique=true) — équivalent de ->where('contrat_id', id)->first() Laravel
    private void processGroupedInvoice(TestOrder order) {
        Invoice invoice = invoiceRepository
                .findFirstByContratIdOrderByCreatedAtDesc(order.getContrat().getId())
                .orElse(null);
        if (invoice == null) {
            throw new BusinessException("CONTRACT_NO_INVOICE");
        }
        if (Boolean.TRUE.equals(invoice.getPaid())) {
            throw new BusinessException("CONTRACT_INVOICE_ALREADY_PAID");
        }
        double newSubtotal = (invoice.getSubtotal() != null ? invoice.getSubtotal() : 0.0)
                + (order.getSubtotal() != null ? order.getSubtotal() : 0.0);
        double newDiscount = (invoice.getDiscount() != null ? invoice.getDiscount() : 0.0)
                + (order.getDiscount() != null ? order.getDiscount() : 0.0);
        BigDecimal currentTotal = invoice.getTotal() != null ? invoice.getTotal() : BigDecimal.ZERO;
        BigDecimal orderTotal = order.getTotal() != null
                ? BigDecimal.valueOf(order.getTotal()) : BigDecimal.ZERO;
        invoice.setSubtotal(newSubtotal);
        invoice.setDiscount(newDiscount);
        invoice.setTotal(currentTotal.add(orderTotal));
        invoiceRepository.save(invoice);
        addInvoiceDetails(invoice, order.getDetails());
    }

    /**
     * Ajoute les lignes de détail non encore facturées à une facture.
     *
     * <p>Un détail est considéré "non facturé" si son champ {@code status} vaut {@code null}
     * ou {@code true}. Une fois traité, il est marqué {@code status=false} pour éviter
     * une double facturation lors d'un rechargement de la transaction.
     *
     * @param invoice la facture cible
     * @param details la liste des détails du bon d'examen
     */
    // Crée les InvoiceDetail pour les détails non encore facturés (status != false)
    private void addInvoiceDetails(Invoice invoice, List<DetailTestOrder> details) {
        for (DetailTestOrder detail : details) {
            if (!Boolean.FALSE.equals(detail.getStatus())) { // null ou true = non facturé
                InvoiceDetail invoiceDetail = new InvoiceDetail();
                invoiceDetail.setInvoice(invoice);
                invoiceDetail.setLabTest(detail.getLabTest());
                invoiceDetail.setTestName(detail.getTestName());
                invoiceDetail.setPrice(detail.getPrice());
                invoiceDetail.setDiscount(detail.getDiscount());
                BigDecimal total = detail.getTotal() != null
                        ? BigDecimal.valueOf(detail.getTotal()) : BigDecimal.ZERO;
                invoiceDetail.setTotal(total);
                invoiceDetail.setUnitPrice(detail.getPrice() != null
                        ? BigDecimal.valueOf(detail.getPrice()) : BigDecimal.ZERO);
                invoiceDetailRepository.save(invoiceDetail);

                detail.setStatus(false); // marquer comme facturé
                // sauvegarde via cascade TestOrder → DetailTestOrder
            }
        }
    }

    /**
     * Génère le prochain code unique de bon d'examen pour la branche et l'année en cours.
     *
     * <p>Format : {@code {préfixe}{aa}-{séq4}} — ex. {@code EX26-0001}.
     * Le préfixe est configurable via le paramètre {@code prefixe_code_demande_examen}
     * dans les réglages de la branche (valeur par défaut : {@code EX}).
     * La séquence repart à {@code 0001} chaque début d'année civile.
     *
     * @param branchId identifiant de la branche pour la numérotation isolée
     * @return le code généré, non encore persisté (peut échouer sur contrainte d'unicité)
     */
    // Algorithme generateCodeExamen — équivalent Java de la fonction PHP Laravel
    // Format : {prefix}{yy2digit}-{seq4}  ex: EX26-0001
    private String generateCodeExamen(UUID branchId) {
        int year = LocalDate.now().getYear();
        List<TestOrder> lastOrders = testOrderRepository.findByBranchIdAndCodeNotNullAndYear(
                branchId, year, PageRequest.of(0, 1));

        String seq;
        if (lastOrders.isEmpty()) {
            seq = "0001";
        } else {
            String lastCode = lastOrders.get(0).getCode();
            String last4 = lastCode.length() >= 4
                    ? lastCode.substring(lastCode.length() - 4) : lastCode;
            try {
                int next = Integer.parseInt(last4) + 1;
                seq = String.format("%04d", next);
            } catch (NumberFormatException e) {
                seq = "0001";
            }
        }

        String prefix = settingRepository
                .findByKeyAndBranchId("prefixe_code_demande_examen", branchId)
                .map(s -> s.getValue() != null ? s.getValue() : "EX")
                .orElse("EX");

        return prefix + (year % 100) + "-" + seq;
    }

    /**
     * Génère le prochain code unique de facture individuelle pour la branche et l'année en cours.
     *
     * <p>Format fixe : {@code FA{aa}{séq4}} — ex. {@code FA260001}.
     * La séquence repart à {@code 0001} chaque début d'année civile.
     *
     * @param branchId identifiant de la branche pour la numérotation isolée
     * @return le code facture généré
     */
    // Algorithme generateCodeFacture — équivalent Java de la fonction PHP Laravel
    // Format : FA{yy2digit}{seq4}  ex: FA260001
    private String generateCodeFacture(UUID branchId) {
        int year = LocalDate.now().getYear();
        List<Invoice> invoices = invoiceRepository.findByBranchIdAndCodeNotNullAndYear(
                branchId, year, PageRequest.of(0, 1));

        String seq;
        if (invoices.isEmpty()) {
            seq = "0001";
        } else {
            String lastCode = invoices.get(0).getCode();
            String last4 = lastCode.length() >= 4
                    ? lastCode.substring(lastCode.length() - 4) : lastCode;
            try {
                int next = Integer.parseInt(last4) + 1;
                seq = String.format("%04d", next);
            } catch (NumberFormatException e) {
                seq = "0001";
            }
        }

        return "FA" + (year % 100) + seq;
    }

    /**
     * Marque un bon d'examen comme livré (passage au statut DELIVERED).
     *
     * <p>Seuls les bons au statut {@code VALIDATED} peuvent être livrés.
     * Positionne également {@code report.isDelivered = true} sur le compte-rendu associé
     * pour tracer la remise physique du résultat.
     *
     * @param id identifiant UUID du bon à livrer
     * @return le bon mis à jour
     */
    @Override
    @Transactional
    public TestOrderResponseDto markAsDelivered(UUID id) {
        TestOrder order = testOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bon d'examen", id));
        if (order.getStatus() != TestOrderStatus.VALIDATED) {
            throw new InvalidOperationException("Seul un bon VALIDATED peut être livré.");
        }
        order.setStatus(TestOrderStatus.DELIVERED);
        testOrderRepository.save(order);

        Report report = reportRepository.findByTestOrderId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compte-rendu du bon", id));
        report.setDelivered(true);
        reportRepository.save(report);

        log.info("Bon d'examen livré: id={}", id);
        return testOrderMapper.toResponseDto(
                testOrderRepository.findById(id).orElseThrow());
    }

    /**
     * Assigne un médecin pathologiste à un bon d'examen.
     *
     * <p>Positionne {@code attribuateDoctorId}, {@code assignedToUserId} (même valeur)
     * et {@code assignmentDate = now()}. Alignement avec le flux Laravel complet
     * (TestOrderController.php lignes 1510–1512).
     *
     * @param id       identifiant UUID du bon
     * @param doctorId identifiant UUID du médecin à assigner
     * @param branchId identifiant de la branche (isolation multi-tenant)
     * @return le bon mis à jour
     */
    @Override
    @Transactional
    public TestOrderResponseDto assignDoctor(UUID id, UUID doctorId, UUID branchId) {
        TestOrder order = testOrderRepository.findByIdAndBranchId(id, branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Bon d'examen", id));
        order.setAttribuateDoctorId(doctorId);
        order.setAssignedToUserId(doctorId);
        order.setAssignmentDate(LocalDateTime.now());
        log.info("Médecin assigné au bon {}: doctorId={}", id, doctorId);
        return testOrderMapper.toResponseDto(testOrderRepository.save(order));
    }

    @Override
    @Transactional
    public java.util.List<String> uploadImages(UUID id, java.util.List<org.springframework.web.multipart.MultipartFile> files) {
        TestOrder order = testOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bon d'examen", id));
        java.util.List<String> existing = parseFilesName(order.getFilesName());
        for (org.springframework.web.multipart.MultipartFile file : files) {
            try {
                existing.add(fileStorageService.store(file));
            } catch (java.io.IOException e) {
                throw new com.labo.anapath.common.exception.BusinessException("Erreur lors du stockage du fichier: " + file.getOriginalFilename());
            }
        }
        try {
            order.setFilesName(objectMapper.writeValueAsString(existing));
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new com.labo.anapath.common.exception.BusinessException("Erreur de sérialisation JSON");
        }
        testOrderRepository.save(order);
        return existing;
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<ImageDto> getImages(UUID id) {
        TestOrder order = testOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bon d'examen", id));
        java.util.List<String> filenames = parseFilesName(order.getFilesName());
        java.util.List<ImageDto> result = new java.util.ArrayList<>();
        for (int i = 0; i < filenames.size(); i++) {
            result.add(new ImageDto(i, filenames.get(i), fileStorageService.getUrl(filenames.get(i))));
        }
        return result;
    }

    @Override
    @Transactional
    public void deleteImage(UUID id, int index) {
        TestOrder order = testOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bon d'examen", id));
        java.util.List<String> filenames = parseFilesName(order.getFilesName());
        if (index < 0 || index >= filenames.size()) {
            throw new InvalidOperationException("Index d'image invalide: " + index);
        }
        try {
            fileStorageService.delete(filenames.get(index));
        } catch (java.io.IOException e) {
            log.warn("Impossible de supprimer le fichier physique à l'index {}: {}", index, e.getMessage());
        }
        filenames.remove(index);
        try {
            order.setFilesName(objectMapper.writeValueAsString(filenames));
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new com.labo.anapath.common.exception.BusinessException("Erreur de sérialisation JSON");
        }
        testOrderRepository.save(order);
    }

    @SuppressWarnings("unchecked")
    private java.util.List<String> parseFilesName(String json) {
        if (json == null || json.isBlank()) return new java.util.ArrayList<>();
        try {
            return objectMapper.readValue(json, java.util.List.class);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            return new java.util.ArrayList<>();
        }
    }
}
