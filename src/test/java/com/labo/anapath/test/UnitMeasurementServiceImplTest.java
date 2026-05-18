package com.labo.anapath.test;

import com.labo.anapath.common.exception.BusinessException;
import com.labo.anapath.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnitMeasurementServiceImplTest {

    @Mock
    private UnitMeasurementRepository unitMeasurementRepository;

    @Mock
    private LabTestRepository labTestRepository;

    @Mock
    private TestCatalogueMapper mapper;

    @InjectMocks
    private UnitMeasurementServiceImpl unitMeasurementService;

    private final UUID BRANCH_ID = UUID.randomUUID();
    private final UUID ID = UUID.randomUUID();

    private UnitMeasurement buildEntity(String name) {
        UnitMeasurement um = new UnitMeasurement();
        um.setName(name);
        um.setAbbreviation("mg/L");
        um.setBranchId(BRANCH_ID);
        return um;
    }

    private UnitMeasurementResponseDto buildResponseDto(String name) {
        return new UnitMeasurementResponseDto(ID, name, "mg/L", BRANCH_ID);
    }

    @Test
    @DisplayName("create - crée une unité et retourne le DTO")
    void create_success_returnsDto() {
        UnitMeasurementRequestDto dto = new UnitMeasurementRequestDto();
        dto.setName("Milligramme par litre");
        dto.setAbbreviation("mg/L");

        UnitMeasurement entity = buildEntity("Milligramme par litre");
        UnitMeasurementResponseDto responseDto = buildResponseDto("Milligramme par litre");

        when(mapper.toUnitMeasurementEntity(dto)).thenReturn(entity);
        when(unitMeasurementRepository.save(any(UnitMeasurement.class))).thenReturn(entity);
        when(mapper.toUnitMeasurementResponseDto(entity)).thenReturn(responseDto);

        UnitMeasurementResponseDto result = unitMeasurementService.create(dto, BRANCH_ID);

        assertThat(result.name()).isEqualTo("Milligramme par litre");
        verify(unitMeasurementRepository).save(entity);
    }

    @Test
    @DisplayName("findById - lève ResourceNotFoundException si inexistant")
    void findById_notFound_throws404() {
        when(unitMeasurementRepository.findById(ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> unitMeasurementService.findById(ID))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("delete - supprime l'unité si aucun lab_test lié")
    void delete_noLinkedLabTests_succeeds() {
        UnitMeasurement entity = buildEntity("Gramme");

        when(unitMeasurementRepository.findById(ID)).thenReturn(Optional.of(entity));
        when(labTestRepository.existsByUnitMeasurement(entity)).thenReturn(false);

        unitMeasurementService.delete(ID);

        verify(unitMeasurementRepository).delete(entity);
    }

    @Test
    @DisplayName("delete - lève BusinessException si des lab_tests sont liés")
    void delete_withLinkedLabTests_throwsBusinessException() {
        UnitMeasurement entity = buildEntity("Gramme");

        when(unitMeasurementRepository.findById(ID)).thenReturn(Optional.of(entity));
        when(labTestRepository.existsByUnitMeasurement(entity)).thenReturn(true);

        assertThatThrownBy(() -> unitMeasurementService.delete(ID))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("référencée par des analyses");
    }

    @Test
    @DisplayName("delete - unité inexistante → ResourceNotFoundException")
    void delete_notFound_throws404() {
        when(unitMeasurementRepository.findById(ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> unitMeasurementService.delete(ID))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
