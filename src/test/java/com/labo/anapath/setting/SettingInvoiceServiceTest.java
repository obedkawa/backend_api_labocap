package com.labo.anapath.setting;

import com.labo.anapath.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SettingInvoiceServiceTest {

    @Mock SettingInvoiceRepository settingInvoiceRepository;
    @Mock SettingInvoiceMapper settingInvoiceMapper;

    SettingInvoiceServiceImpl service;

    private final UUID SI_ID = UUID.randomUUID();

    @BeforeEach
    void setup() {
        service = new SettingInvoiceServiceImpl(settingInvoiceRepository, settingInvoiceMapper);
    }

    private SettingInvoice buildSettingInvoice(String ifu, String token, Boolean status) {
        SettingInvoice si = new SettingInvoice();
        ReflectionTestUtils.setField(si, "id", SI_ID);
        si.setIfu(ifu);
        si.setToken(token);
        si.setStatus(status);
        return si;
    }

    private SettingInvoiceResponseDto dummyDto(String ifu, String token, Boolean status) {
        return new SettingInvoiceResponseDto(SI_ID, ifu, token, status, UUID.randomUUID(), null, null);
    }

    @Test
    @DisplayName("update - données valides → met à jour tous les champs fournis")
    void update_validData_updatesAllFields() {
        SettingInvoice existing = buildSettingInvoice("000000000", "old-token", false);
        when(settingInvoiceRepository.findById(SI_ID)).thenReturn(Optional.of(existing));
        when(settingInvoiceRepository.save(any())).thenReturn(existing);
        when(settingInvoiceMapper.toResponseDto(existing))
                .thenReturn(dummyDto("123456789012", "new-token", true));

        SettingInvoiceRequestDto dto = new SettingInvoiceRequestDto();
        dto.setIfu("123456789012");
        dto.setToken("new-token");
        dto.setStatus(true);

        SettingInvoiceResponseDto result = service.update(SI_ID, dto);

        assertThat(existing.getIfu()).isEqualTo("123456789012");
        assertThat(existing.getToken()).isEqualTo("new-token");
        assertThat(existing.getStatus()).isTrue();
        assertThat(result.status()).isTrue();
    }

    @Test
    @DisplayName("update - ID inconnu → ResourceNotFoundException")
    void update_notFound_throwsResourceNotFoundException() {
        when(settingInvoiceRepository.findById(SI_ID)).thenReturn(Optional.empty());

        SettingInvoiceRequestDto dto = new SettingInvoiceRequestDto();
        dto.setStatus(true);

        assertThatThrownBy(() -> service.update(SI_ID, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("update - status=false → désactive la normalisation MECeF")
    void update_statusFalse_deactivatesMecef() {
        SettingInvoice existing = buildSettingInvoice("123", "token", true);
        when(settingInvoiceRepository.findById(SI_ID)).thenReturn(Optional.of(existing));
        when(settingInvoiceRepository.save(any())).thenReturn(existing);
        when(settingInvoiceMapper.toResponseDto(existing))
                .thenReturn(dummyDto("123", "token", false));

        SettingInvoiceRequestDto dto = new SettingInvoiceRequestDto();
        dto.setStatus(false);

        SettingInvoiceResponseDto result = service.update(SI_ID, dto);

        assertThat(existing.getStatus()).isFalse();
        assertThat(result.status()).isFalse();
    }

    @Test
    @DisplayName("update - champs null → ne modifie pas les valeurs existantes")
    void update_nullFields_preservesExistingValues() {
        SettingInvoice existing = buildSettingInvoice("111", "token-preserve", true);
        when(settingInvoiceRepository.findById(SI_ID)).thenReturn(Optional.of(existing));
        when(settingInvoiceRepository.save(any())).thenReturn(existing);
        when(settingInvoiceMapper.toResponseDto(existing))
                .thenReturn(dummyDto("111", "token-preserve", true));

        SettingInvoiceRequestDto dto = new SettingInvoiceRequestDto();
        // all fields null

        service.update(SI_ID, dto);

        assertThat(existing.getIfu()).isEqualTo("111");
        assertThat(existing.getToken()).isEqualTo("token-preserve");
        assertThat(existing.getStatus()).isTrue();
    }
}
