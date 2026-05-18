package com.labo.anapath.setting;

import com.labo.anapath.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SettingAppServiceTest {

    @Mock SettingAppRepository settingAppRepository;
    @Mock SettingAppMapper settingAppMapper;

    SettingAppServiceImpl service;

    private final UUID BRANCH_ID = UUID.randomUUID();
    private final UUID SA_ID     = UUID.randomUUID();

    @BeforeEach
    void setup() {
        service = new SettingAppServiceImpl(settingAppRepository, settingAppMapper);
    }

    private SettingApp buildSettingApp(String key, String value) {
        SettingApp sa = new SettingApp();
        ReflectionTestUtils.setField(sa, "id", SA_ID);
        sa.setKey(key);
        sa.setValue(value);
        return sa;
    }

    private SettingAppResponseDto dummyDto(String key, String value) {
        return new SettingAppResponseDto(SA_ID, key, value, null, BRANCH_ID);
    }

    @Test
    @DisplayName("upsert - clé nouvelle → création d'un nouveau SettingApp")
    void upsert_newKey_createsSettingApp() {
        when(settingAppRepository.findByKeyAndBranchId("lab_name", BRANCH_ID))
                .thenReturn(Optional.empty());
        SettingApp saved = buildSettingApp("lab_name", "Labo Anapath");
        when(settingAppRepository.save(any())).thenReturn(saved);
        when(settingAppMapper.toResponseDto(saved)).thenReturn(dummyDto("lab_name", "Labo Anapath"));

        SettingAppRequestDto dto = new SettingAppRequestDto();
        dto.setKey("lab_name");
        dto.setValue("Labo Anapath");

        SettingAppResponseDto result = service.upsert(dto, BRANCH_ID);

        assertThat(result.key()).isEqualTo("lab_name");
        assertThat(result.value()).isEqualTo("Labo Anapath");
    }

    @Test
    @DisplayName("upsert - clé existante → mise à jour de la valeur")
    void upsert_existingKey_updatesValue() {
        SettingApp existing = buildSettingApp("lab_name", "Ancien nom");
        when(settingAppRepository.findByKeyAndBranchId("lab_name", BRANCH_ID))
                .thenReturn(Optional.of(existing));
        when(settingAppRepository.save(any())).thenReturn(existing);
        when(settingAppMapper.toResponseDto(existing)).thenReturn(dummyDto("lab_name", "Nouveau nom"));

        SettingAppRequestDto dto = new SettingAppRequestDto();
        dto.setKey("lab_name");
        dto.setValue("Nouveau nom");

        SettingAppResponseDto result = service.upsert(dto, BRANCH_ID);

        assertThat(existing.getValue()).isEqualTo("Nouveau nom");
        assertThat(result.value()).isEqualTo("Nouveau nom");
    }

    @Test
    @DisplayName("findById - ID inconnu → ResourceNotFoundException")
    void findById_notFound_throwsResourceNotFoundException() {
        when(settingAppRepository.findById(SA_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(SA_ID))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("findAll - retourne page paginée par branche")
    void findAll_returnsPaginatedByBranch() {
        SettingApp sa = buildSettingApp("logo", "/path/logo.png");
        Page<SettingApp> page = new PageImpl<>(List.of(sa));
        when(settingAppRepository.findByBranchId(eq(BRANCH_ID), any(Pageable.class))).thenReturn(page);
        when(settingAppMapper.toResponseDto(sa)).thenReturn(dummyDto("logo", "/path/logo.png"));

        var result = service.findAll(0, 50, BRANCH_ID);

        assertThat(result).isNotNull();
    }
}
