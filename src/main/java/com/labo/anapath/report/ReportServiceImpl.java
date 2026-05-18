package com.labo.anapath.report;

import com.labo.anapath.common.dto.PageResponse;
import com.labo.anapath.common.exception.InvalidOperationException;
import com.labo.anapath.common.exception.ResourceNotFoundException;
import com.labo.anapath.setting.SettingReportTemplate;
import com.labo.anapath.setting.SettingReportTemplateRepository;
import com.labo.anapath.testorder.TestOrderRepository;
import com.labo.anapath.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Implémentation du service de gestion des comptes-rendus anatomopathologiques.
 *
 * <p>Gère le cycle de vie complet des CRs : création, consultation, mise à jour,
 * validation, livraison et journalisation obligatoire dans {@link LogReport}.
 * Chaque action significative est tracée conformément aux exigences réglementaires
 * de traçabilité en anatomopathologie.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final LogReportRepository logReportRepository;
    private final TagRepository tagRepository;
    private final TitleReportRepository titleReportRepository;
    private final TestOrderRepository testOrderRepository;
    private final UserRepository userRepository;
    private final SettingReportTemplateRepository templateRepository;
    private final ReportMapper reportMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ReportResponseDto> findAll(int page, int size, UUID branchId) {
        return PageResponse.of(reportRepository.findByBranchId(branchId,
                PageRequest.of(page, size, Sort.by("createdAt").descending()))
                .map(reportMapper::toResponseDto));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ReportResponseDto> findAll(int page, int size, UUID branchId, Integer month, Integer year, UUID doctorId) {
        return PageResponse.of(reportRepository.findFiltered(branchId, month, year, doctorId,
                PageRequest.of(page, size))
                .map(reportMapper::toResponseDto));
    }

    @Override
    @Transactional(readOnly = true)
    public ReportResponseDto findById(UUID id) {
        return reportMapper.toResponseDto(reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compte-rendu", id)));
    }

    @Override
    @Transactional(readOnly = true)
    public ReportDetailDto findDetailById(UUID id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compte-rendu", id));

        List<LogReport> logs = logReportRepository.findByReportIdOrderByCreatedAtDesc(id);
        List<ReportDetailDto.LogReportDto> logDtos = logs.stream()
                .map(l -> new ReportDetailDto.LogReportDto(
                        l.getAction(),
                        l.getDescription(),
                        l.getUser() != null ? l.getUser().getFirstname() + " " + l.getUser().getLastname() : null,
                        l.getCreatedAt()))
                .toList();

        String patientName = null;
        if (report.getTestOrder() != null && report.getTestOrder().getPatient() != null) {
            var p = report.getTestOrder().getPatient();
            patientName = p.getFirstname() + " " + p.getLastname();
        }

        return new ReportDetailDto(
                report.getId(), report.getCode(),
                report.getTestOrder() != null ? report.getTestOrder().getId() : null,
                report.getTestOrder() != null ? report.getTestOrder().getCode() : null,
                patientName,
                report.getTitleReport() != null ? report.getTitleReport().getId() : null,
                report.getTitleReport() != null ? report.getTitleReport().getName() : null,
                report.getContent(), report.getContentMicro(),
                report.getComment(), report.getCommentSup(),
                report.getDescriptionSupplementaire(), report.getDescriptionSupplementaireMicro(),
                report.getStatus(),
                report.isDelivered(), report.isCalled(),
                report.getReceiverName(),
                report.getSignatureDate(), report.getDeliveryDate(), report.getCallDate(),
                report.getSignatory1() != null ? report.getSignatory1().getId() : null,
                report.getSignatory1() != null ? report.getSignatory1().getFirstname() + " " + report.getSignatory1().getLastname() : null,
                report.getSignatory2() != null ? report.getSignatory2().getId() : null,
                report.getSignatory2() != null ? report.getSignatory2().getFirstname() + " " + report.getSignatory2().getLastname() : null,
                report.getSignatory3() != null ? report.getSignatory3().getId() : null,
                report.getSignatory3() != null ? report.getSignatory3().getFirstname() + " " + report.getSignatory3().getLastname() : null,
                report.getReviewedBy() != null ? report.getReviewedBy().getId() : null,
                report.getReviewedBy() != null ? report.getReviewedBy().getFirstname() + " " + report.getReviewedBy().getLastname() : null,
                report.getTags().stream().map(Tag::getName).toList(),
                logDtos,
                report.getCreatedAt(), report.getUpdatedAt());
    }

    @Override
    @Transactional
    public ReportResponseDto createOrUpdate(ReportRequestDto dto, UUID branchId) {
        Report report;
        boolean isCreate = dto.getReportId() == null;

        if (!isCreate) {
            report = reportRepository.findById(dto.getReportId())
                    .orElseThrow(() -> new ResourceNotFoundException("Compte-rendu", dto.getReportId()));
            if (report.getStatus() == ReportStatus.DELIVERED) {
                throw new InvalidOperationException("Impossible de modifier un rapport livré.");
            }
        } else {
            report = new Report();
            report.setBranchId(branchId);
            if (dto.getTestOrderId() == null) {
                throw new InvalidOperationException("Le bon d'examen est obligatoire à la création.");
            }
            report.setTestOrder(testOrderRepository.findById(dto.getTestOrderId())
                    .orElseThrow(() -> new ResourceNotFoundException("Bon d'examen", dto.getTestOrderId())));
        }

        report.setContent(dto.getContent());
        report.setContentMicro(dto.getContentMicro());
        report.setComment(dto.getComment());
        report.setCommentSup(dto.getCommentSup());
        report.setDescriptionSupplementaire(dto.getDescriptionSupplementaire());
        report.setDescriptionSupplementaireMicro(dto.getDescriptionSupplementaireMicro());

        if (dto.getTitleId() != null) {
            report.setTitleReport(titleReportRepository.findById(dto.getTitleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Titre", dto.getTitleId())));
        }
        if (dto.getReviewedById() != null) {
            report.setReviewedBy(userRepository.findById(dto.getReviewedById())
                    .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", dto.getReviewedById())));
        }
        if (dto.getSignatory1Id() != null) {
            report.setSignatory1(userRepository.findById(dto.getSignatory1Id())
                    .orElseThrow(() -> new ResourceNotFoundException("Signataire 1", dto.getSignatory1Id())));
        }
        if (dto.getSignatory2Id() != null) {
            report.setSignatory2(userRepository.findById(dto.getSignatory2Id())
                    .orElseThrow(() -> new ResourceNotFoundException("Signataire 2", dto.getSignatory2Id())));
        }
        if (dto.getSignatory3Id() != null) {
            report.setSignatory3(userRepository.findById(dto.getSignatory3Id())
                    .orElseThrow(() -> new ResourceNotFoundException("Signataire 3", dto.getSignatory3Id())));
        }

        if ("VALIDATED".equalsIgnoreCase(dto.getStatus())) {
            report.setStatus(ReportStatus.VALIDATED);
            report.setSignatureDate(LocalDateTime.now());
            report.setDeliveryDate(LocalDateTime.now());
            if (dto.getSignatory1Id() != null && report.getTestOrder() != null) {
                report.getTestOrder().setAssignedToUserId(dto.getSignatory1Id());
                testOrderRepository.save(report.getTestOrder());
            }
        } else {
            report.setStatus(ReportStatus.DRAFT);
        }

        // Sync COMPLET des tags : delete + re-insert
        report.getTags().clear();
        if (dto.getTagIds() != null && !dto.getTagIds().isEmpty()) {
            report.setTags(tagRepository.findAllById(dto.getTagIds()));
        }

        Report saved = reportRepository.save(report);
        logAction(saved.getId(), isCreate ? "CREATE" : "UPDATE", branchId);
        return reportMapper.toResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ReportSuiviDto getSuivi(UUID branchId, Integer month, Integer year) {
        Object[] examenRaw = reportRepository.getExamenStats(branchId, month, year);
        Object[] rapportRaw = reportRepository.getRapportStats(branchId);
        Object[] calledRaw = reportRepository.getPatientCalledStats(branchId);
        List<Integer> years = reportRepository.findAvailableYears(branchId);
        Long macroCount = reportRepository.countMacrosWithOrders(branchId);

        Object[] ex = safeRow(examenRaw, 5);
        Object[] rp = safeRow(rapportRaw, 3);
        Object[] ca = safeRow(calledRaw, 4);

        ReportSuiviDto.ExamenStats examens = new ReportSuiviDto.ExamenStats(
                toLong(ex[0]), toLong(ex[1]), toLong(ex[2]), toLong(ex[3]), toLong(ex[4]));
        ReportSuiviDto.RapportStats rapports = new ReportSuiviDto.RapportStats(
                toLong(rp[0]), toLong(rp[1]), toLong(rp[2]));
        ReportSuiviDto.PatientCalledStats calledStats = new ReportSuiviDto.PatientCalledStats(
                toLong(ca[0]), toLong(ca[1]), toLong(ca[2]), toLong(ca[3]));

        return new ReportSuiviDto(examens, rapports,
                new ReportSuiviDto.MacroStats(macroCount != null ? macroCount : 0L), calledStats, years);
    }

    private Object[] safeRow(Object[] raw, int expectedCols) {
        if (raw == null || raw.length == 0) return new Object[expectedCols];
        // Native query single-row: raw is the row itself
        if (raw[0] instanceof Object[] nested) return nested;
        return raw;
    }

    private long toLong(Object val) {
        if (val == null) return 0L;
        if (val instanceof Number n) return n.longValue();
        return Long.parseLong(val.toString());
    }

    @Override
    @Transactional
    public ReportResponseDto create(ReportRequestDto dto, UUID branchId) {
        Report report = new Report();
        report.setBranchId(branchId);
        report.setContent(dto.getContent());
        report.setComment(dto.getComment());
        report.setReceiverName(dto.getReceiverName());
        report.setStatus(ReportStatus.DRAFT);

        report.setTestOrder(testOrderRepository.findById(dto.getTestOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Bon d'examen", dto.getTestOrderId())));

        if (dto.getTitleId() != null) {
            report.setTitleReport(titleReportRepository.findById(dto.getTitleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Titre", dto.getTitleId())));
        }
        if (dto.getReviewedById() != null) {
            report.setReviewedBy(userRepository.findById(dto.getReviewedById())
                    .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", dto.getReviewedById())));
        }

        if (dto.getTagIds() != null && !dto.getTagIds().isEmpty()) {
            List<Tag> tags = tagRepository.findAllById(dto.getTagIds());
            report.setTags(tags);
        }

        Report saved = reportRepository.save(report);
        logAction(saved.getId(), "CREATE", branchId);
        return reportMapper.toResponseDto(saved);
    }

    /**
     * Met à jour le contenu textuel et le commentaire d'un compte-rendu.
     * Un CR au statut DELIVERED ne peut plus être modifié.
     *
     * @param id  identifiant UUID du CR
     * @param dto nouvelles données
     * @return le CR mis à jour
     */
    @Override
    @Transactional
    public ReportResponseDto update(UUID id, ReportRequestDto dto) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compte-rendu", id));
        if (report.getStatus() == ReportStatus.DELIVERED) {
            throw new InvalidOperationException("Impossible de modifier un compte-rendu déjà livré.");
        }
        report.setContent(dto.getContent());
        report.setComment(dto.getComment());
        return reportMapper.toResponseDto(reportRepository.save(report));
    }

    /**
     * Supprime (soft delete) un compte-rendu.
     *
     * @param id identifiant UUID du CR à supprimer
     */
    @Override
    @Transactional
    public void delete(UUID id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compte-rendu", id));
        reportRepository.delete(report);
    }

    /**
     * Valide un compte-rendu en passant son statut à VALIDATED et journalise l'action.
     *
     * @param id     identifiant UUID du CR
     * @param userId identifiant de l'utilisateur validant le CR
     * @return le CR validé
     */
    @Override
    @Transactional
    public ReportResponseDto validate(UUID id, UUID userId) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compte-rendu", id));
        if (report.getStatus() == ReportStatus.VALIDATED || report.getStatus() == ReportStatus.DELIVERED) {
            throw new InvalidOperationException("Le rapport est déjà validé ou livré.");
        }
        report.setStatus(ReportStatus.VALIDATED);
        report.setSignatureDate(LocalDateTime.now());
        report.setDeliveryDate(LocalDateTime.now());
        Report saved = reportRepository.save(report);
        logAction(id, "Validé", userId);
        return reportMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    public ReportResponseDto deliver(UUID id, String receiverName, UUID userId) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compte-rendu", id));
        if (report.getStatus() != ReportStatus.VALIDATED) {
            throw new InvalidOperationException("Le compte-rendu doit être validé avant d'être livré.");
        }
        report.setStatus(ReportStatus.DELIVERED);
        report.setDelivered(true);
        report.setReceiverName(receiverName);
        Report saved = reportRepository.save(report);
        logAction(id, "Livré", userId);
        return reportMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    public ReportResponseDto markDelivered(UUID id, UUID userId) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compte-rendu", id));
        report.setDelivered(true);
        report.setDeliveryDate(LocalDateTime.now());
        Report saved = reportRepository.save(report);
        logAction(id, "Livré", userId);
        return reportMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    public ReportResponseDto markInformed(UUID id, UUID userId) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compte-rendu", id));
        report.setCalled(true);
        report.setCallDate(LocalDateTime.now());
        Report saved = reportRepository.save(report);
        logAction(id, "Informé", userId);
        return reportMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    public ReportResponseDto storeSignature(UUID id, StoreSignatureRequestDto dto, UUID userId) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compte-rendu", id));
        // RÈGLE R5 : isDelivered ET isCalled positionnés SIMULTANÉMENT dans la même transaction
        report.setDelivered(true);
        report.setDeliveryDate(LocalDateTime.now());
        report.setCalled(true);
        report.setCallDate(LocalDateTime.now());
        report.setRetrieverName(dto.getSignatorName());
        report.setRetrieverSignature(dto.getSignature());
        Report saved = reportRepository.save(report);
        logAction(id, "Signature enregistrée", userId);
        return reportMapper.toResponseDto(saved);
    }

    /**
     * Enregistre une entrée dans le journal de traçabilité {@link LogReport}.
     *
     * <p>Si le CR ou l'utilisateur n'existe pas, l'action est silencieusement ignorée
     * (usage de {@code ifPresent}) pour ne pas bloquer les opérations principales.
     *
     * @param reportId identifiant du CR concerné
     * @param action   libellé de l'action (ex. {@code "CREATE"}, {@code "VALIDATE"}, {@code "DELIVER"})
     * @param userId   identifiant de l'auteur de l'action
     */
    @Override
    @Transactional(readOnly = true)
    public SettingReportTemplate getTemplate(UUID reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Compte-rendu", reportId));
        if (report.getTemplateId() == null) {
            throw new ResourceNotFoundException("Template", reportId);
        }
        return templateRepository.findById(report.getTemplateId())
                .orElseThrow(() -> new ResourceNotFoundException("Template", report.getTemplateId()));
    }

    @Override
    @Transactional
    public ReportResponseDto setTemplate(UUID reportId, UUID templateId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Compte-rendu", reportId));
        templateRepository.findById(templateId)
                .orElseThrow(() -> new ResourceNotFoundException("Template", templateId));
        report.setTemplateId(templateId);
        return reportMapper.toResponseDto(reportRepository.save(report));
    }

    @Override
    @Transactional
    public void logAction(UUID reportId, String action, UUID userId) {
        reportRepository.findById(reportId).ifPresent(report -> {
            LogReport logReport = new LogReport();
            logReport.setBranchId(report.getBranchId());
            logReport.setReport(report);
            logReport.setAction(action);
            logReport.setDescription("Action: " + action + " on report: " + reportId);
            userRepository.findById(userId).ifPresent(logReport::setUser);
            logReportRepository.save(logReport);
        });
    }
}
