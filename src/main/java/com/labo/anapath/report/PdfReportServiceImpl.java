package com.labo.anapath.report;

import com.labo.anapath.common.exception.InvalidOperationException;
import com.labo.anapath.common.exception.ResourceNotFoundException;
import com.labo.anapath.setting.SettingApp;
import com.labo.anapath.setting.SettingAppRepository;
import com.labo.anapath.user.UserRepository;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PdfReportServiceImpl implements PdfReportService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final ReportRepository reportRepository;
    private final LogReportRepository logReportRepository;
    private final SettingAppRepository settingAppRepository;
    private final UserRepository userRepository;
    private final QrCodeService qrCodeService;
    private final SpringTemplateEngine templateEngine;

    @Override
    @Transactional
    public byte[] generatePdf(UUID reportId, UUID userId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Compte-rendu", reportId));

        Context ctx = new Context();

        ctx.setVariable("code", report.getCode());
        ctx.setVariable("testOrderCode",
                report.getTestOrder() != null ? report.getTestOrder().getCode() : "");
        ctx.setVariable("signatureDate",
                report.getSignatureDate() != null ? report.getSignatureDate().format(DATE_FMT) : "");
        ctx.setVariable("prelevementDate",
                report.getTestOrder() != null && report.getTestOrder().getPrelevementDate() != null
                        ? report.getTestOrder().getPrelevementDate().format(DATE_FMT) : "");
        ctx.setVariable("testAffiliate",
                report.getTestOrder() != null ? report.getTestOrder().getTestAffiliate() : "");

        // QR Code
        String qrCode = "";
        try {
            if (report.getTestOrder() != null) {
                qrCode = qrCodeService.generateBase64(report.getTestOrder().getCode(), 200);
            }
        } catch (Exception e) {
            log.warn("QR code generation failed: {}", e.getMessage());
        }
        ctx.setVariable("qrcode", qrCode);

        ctx.setVariable("title",
                report.getTitleReport() != null ? report.getTitleReport().getName() : "");
        ctx.setVariable("content", report.getContent() != null ? report.getContent() : "");
        ctx.setVariable("contentMicro", report.getContentMicro() != null ? report.getContentMicro() : "");
        ctx.setVariable("contentSupplementaire",
                report.getDescriptionSupplementaire() != null ? report.getDescriptionSupplementaire() : "");
        ctx.setVariable("contentSupplementaireMicro",
                report.getDescriptionSupplementaireMicro() != null ? report.getDescriptionSupplementaireMicro() : "");

        // Patient
        if (report.getTestOrder() != null && report.getTestOrder().getPatient() != null) {
            var patient = report.getTestOrder().getPatient();
            ctx.setVariable("patientFirstname", patient.getFirstname());
            ctx.setVariable("patientLastname", patient.getLastname());
            ctx.setVariable("patientAge", patient.getAge() != null ? patient.getAge() : "");
            ctx.setVariable("patientAgeUnit",
                    Boolean.TRUE.equals(patient.getYearOrMonth()) ? "ans" : "mois");
            ctx.setVariable("patientGenre", patient.getGenre() != null ? patient.getGenre() : "");
        } else {
            ctx.setVariable("patientFirstname", "");
            ctx.setVariable("patientLastname", "");
            ctx.setVariable("patientAge", "");
            ctx.setVariable("patientAgeUnit", "ans");
            ctx.setVariable("patientGenre", "");
        }

        // Signataires
        if (report.getSignatory1() != null) {
            ctx.setVariable("signator",
                    report.getSignatory1().getFirstname() + " " + report.getSignatory1().getLastname());
            ctx.setVariable("signature1", report.getSignatory1().getSignature() != null
                    ? report.getSignatory1().getSignature() : "");
        } else {
            ctx.setVariable("signator", "");
            ctx.setVariable("signature1", "");
        }
        ctx.setVariable("signatory2Name", report.getSignatory2() != null
                ? report.getSignatory2().getFirstname() + " " + report.getSignatory2().getLastname() : "");
        ctx.setVariable("signatory3Name", report.getSignatory3() != null
                ? report.getSignatory3().getFirstname() + " " + report.getSignatory3().getLastname() : "");
        ctx.setVariable("reviewedBy", report.getReviewedBy() != null
                ? report.getReviewedBy().getFirstname() + " " + report.getReviewedBy().getLastname() : "");

        // Médecin et hôpital
        ctx.setVariable("doctorName", report.getTestOrder() != null && report.getTestOrder().getDoctor() != null
                ? report.getTestOrder().getDoctor().getName() : "");
        ctx.setVariable("hospitalName", report.getTestOrder() != null && report.getTestOrder().getHospital() != null
                ? report.getTestOrder().getHospital().getName() : "");

        // Settings
        ctx.setVariable("entete", settingAppRepository.findByKey("entete")
                .map(SettingApp::getValue).orElse(""));
        ctx.setVariable("footer", settingAppRepository.findByKey("report_footer")
                .map(SettingApp::getValue).orElse(""));
        ctx.setVariable("reportReviewTitle", settingAppRepository.findByKey("report_review_title")
                .map(SettingApp::getValue).orElse("Relu par"));

        // Render HTML
        String html = templateEngine.process("pdf/rapport", ctx);

        // Convert to PDF
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(html, null);
            builder.toStream(outputStream);
            builder.run();

            // Log
            LogReport logReport = new LogReport();
            logReport.setBranchId(report.getBranchId());
            logReport.setReport(report);
            logReport.setAction("Imprimer");
            logReport.setDescription("PDF généré pour le rapport " + report.getCode());
            userRepository.findById(userId).ifPresent(logReport::setUser);
            logReportRepository.save(logReport);

            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new InvalidOperationException("Erreur lors de la génération du PDF: " + e.getMessage());
        }
    }
}
