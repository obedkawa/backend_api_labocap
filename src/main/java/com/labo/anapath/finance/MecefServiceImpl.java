package com.labo.anapath.finance;

import com.labo.anapath.common.exception.ExternalApiException;
import com.labo.anapath.common.exception.InvalidOperationException;
import com.labo.anapath.common.exception.ResourceNotFoundException;
import com.labo.anapath.setting.SettingInvoice;
import com.labo.anapath.setting.SettingInvoiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MecefServiceImpl implements MecefService {

    private static final String MECEF_BASE_URL = "https://developper.impots.bj/sygmef-emcf/api/invoice";

    private final InvoiceRepository invoiceRepository;
    private final SettingInvoiceRepository settingInvoiceRepository;
    private final FinanceMapper financeMapper;
    private final RestTemplate restTemplate;

    @Override
    @Transactional
    public InvoiceResponseDto confirmInvoice(UUID invoiceId, String uid, UUID branchId) {
        SettingInvoice setting = requireMecefEnabled(branchId);

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Facture", invoiceId));

        MecefApiResponse mecefResponse;
        try {
            mecefResponse = callMecef(uid + "/confirm", setting.getToken(), HttpMethod.PUT, MecefApiResponse.class);
        } catch (RestClientException e) {
            log.error("MECeF confirm API indisponible: {}", e.getMessage());
            throw new ExternalApiException("MECeF indisponible", e);
        }

        invoice.setPaid(true);
        invoice.setStatus(InvoiceStatus.PAID);
        if (mecefResponse != null) {
            invoice.setCodeMecef(mecefResponse.getCodeMECeFDGI());
            invoice.setCounters(mecefResponse.getCounters());
            invoice.setDateGenerate(mecefResponse.getDateTime());
            invoice.setNim(mecefResponse.getNim());
            invoice.setQrcode(mecefResponse.getQrCode());
        }

        return financeMapper.toInvoiceResponseDto(invoiceRepository.save(invoice));
    }

    @Override
    @Transactional
    public void cancelInvoice(UUID invoiceId, String uid, UUID branchId) {
        SettingInvoice setting = requireMecefEnabled(branchId);

        try {
            callMecef(uid + "/cancel", setting.getToken(), HttpMethod.PUT, Void.class);
        } catch (RestClientException e) {
            log.error("MECeF cancel API indisponible: {}", e.getMessage());
            throw new ExternalApiException("MECeF indisponible", e);
        }
    }

    private SettingInvoice requireMecefEnabled(UUID branchId) {
        SettingInvoice setting = settingInvoiceRepository.findByBranchId(branchId)
                .orElseThrow(() -> new InvalidOperationException("MECEF_DISABLED"));
        if (!Boolean.TRUE.equals(setting.getStatus())) {
            throw new InvalidOperationException("MECEF_DISABLED");
        }
        return setting;
    }

    private <T> T callMecef(String path, String token, HttpMethod method, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        ResponseEntity<T> response = restTemplate.exchange(
                MECEF_BASE_URL + "/" + path,
                method,
                new HttpEntity<>(headers),
                responseType);
        return response.getBody();
    }
}
