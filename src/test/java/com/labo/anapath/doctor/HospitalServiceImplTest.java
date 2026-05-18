package com.labo.anapath.doctor;

import com.labo.anapath.common.dto.PageResponse;
import com.labo.anapath.common.exception.DuplicateResourceException;
import com.labo.anapath.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HospitalServiceImplTest {

    @Mock
    private HospitalRepository hospitalRepository;

    @Mock
    private HospitalMapper hospitalMapper;

    @InjectMocks
    private HospitalServiceImpl hospitalService;

    private final UUID BRANCH_ID = UUID.randomUUID();
    private final UUID HOSPITAL_ID = UUID.randomUUID();

    private Hospital buildHospital(String name) {
        Hospital h = new Hospital();
        h.setName(name);
        h.setTelephone("+229 00000000");
        h.setAdresse("Rue Test");
        h.setBranchId(BRANCH_ID);
        return h;
    }

    private HospitalResponseDto buildResponseDto(String name) {
        return new HospitalResponseDto(HOSPITAL_ID, name, "+229 00000000", "Rue Test", null, null, BRANCH_ID, LocalDateTime.now());
    }

    @Test
    @DisplayName("create - crée un hôpital et retourne le DTO")
    void create_success_returnsDto() {
        HospitalRequestDto dto = new HospitalRequestDto();
        dto.setName("CHU de Cotonou");

        Hospital hospital = buildHospital("CHU de Cotonou");
        HospitalResponseDto responseDto = buildResponseDto("CHU de Cotonou");

        when(hospitalRepository.existsByNameIgnoreCaseAndBranchId("CHU de Cotonou", BRANCH_ID)).thenReturn(false);
        when(hospitalMapper.toEntity(dto)).thenReturn(hospital);
        when(hospitalRepository.save(any(Hospital.class))).thenReturn(hospital);
        when(hospitalMapper.toResponseDto(hospital)).thenReturn(responseDto);

        HospitalResponseDto result = hospitalService.create(dto, BRANCH_ID);

        assertThat(result.name()).isEqualTo("CHU de Cotonou");
        verify(hospitalRepository).save(hospital);
    }

    @Test
    @DisplayName("create - nom en doublon → DuplicateResourceException")
    void create_duplicateName_throws409() {
        HospitalRequestDto dto = new HospitalRequestDto();
        dto.setName("CHU de Cotonou");

        when(hospitalRepository.existsByNameIgnoreCaseAndBranchId("CHU de Cotonou", BRANCH_ID)).thenReturn(true);

        assertThatThrownBy(() -> hospitalService.create(dto, BRANCH_ID))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    @DisplayName("findAll - retourne une page paginée")
    void findAll_returnsPaginatedResults() {
        Hospital hospital = buildHospital("CHU de Cotonou");
        HospitalResponseDto dto = buildResponseDto("CHU de Cotonou");
        Page<Hospital> page = new PageImpl<>(List.of(hospital));

        when(hospitalRepository.findByBranchId(any(UUID.class), any(Pageable.class))).thenReturn(page);
        when(hospitalMapper.toResponseDto(hospital)).thenReturn(dto);

        PageResponse<HospitalResponseDto> result = hospitalService.findAll(0, 20, BRANCH_ID);

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).name()).isEqualTo("CHU de Cotonou");
    }

    @Test
    @DisplayName("findById - retourne le DTO quand l'hôpital existe")
    void findById_found_returnsDto() {
        Hospital hospital = buildHospital("CHU de Cotonou");
        HospitalResponseDto dto = buildResponseDto("CHU de Cotonou");

        when(hospitalRepository.findById(HOSPITAL_ID)).thenReturn(Optional.of(hospital));
        when(hospitalMapper.toResponseDto(hospital)).thenReturn(dto);

        HospitalResponseDto result = hospitalService.findById(HOSPITAL_ID);

        assertThat(result.name()).isEqualTo("CHU de Cotonou");
    }

    @Test
    @DisplayName("findById - lève ResourceNotFoundException si inexistant")
    void findById_notFound_throws404() {
        when(hospitalRepository.findById(HOSPITAL_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> hospitalService.findById(HOSPITAL_ID))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("update - met à jour l'hôpital et retourne le DTO")
    void update_success_returnsUpdatedDto() {
        HospitalRequestDto dto = new HospitalRequestDto();
        dto.setName("Clinique Sainte Marie");

        Hospital hospital = buildHospital("CHU de Cotonou");
        HospitalResponseDto responseDto = buildResponseDto("Clinique Sainte Marie");

        when(hospitalRepository.findById(HOSPITAL_ID)).thenReturn(Optional.of(hospital));
        when(hospitalRepository.existsByNameIgnoreCaseAndBranchIdAndIdNot("Clinique Sainte Marie", BRANCH_ID, HOSPITAL_ID)).thenReturn(false);
        when(hospitalRepository.save(any(Hospital.class))).thenReturn(hospital);
        when(hospitalMapper.toResponseDto(hospital)).thenReturn(responseDto);

        HospitalResponseDto result = hospitalService.update(HOSPITAL_ID, dto);

        assertThat(result.name()).isEqualTo("Clinique Sainte Marie");
        verify(hospitalMapper).updateEntityFromDto(dto, hospital);
    }

    @Test
    @DisplayName("update - doublon de nom sur un autre hôpital → DuplicateResourceException")
    void update_duplicateName_throws409() {
        HospitalRequestDto dto = new HospitalRequestDto();
        dto.setName("Clinique Existante");

        Hospital hospital = buildHospital("CHU de Cotonou");

        when(hospitalRepository.findById(HOSPITAL_ID)).thenReturn(Optional.of(hospital));
        when(hospitalRepository.existsByNameIgnoreCaseAndBranchIdAndIdNot("Clinique Existante", BRANCH_ID, HOSPITAL_ID)).thenReturn(true);

        assertThatThrownBy(() -> hospitalService.update(HOSPITAL_ID, dto))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    @DisplayName("delete - supprime l'hôpital → soft delete")
    void delete_callsRepositoryDelete() {
        Hospital hospital = buildHospital("CHU de Cotonou");

        when(hospitalRepository.findById(HOSPITAL_ID)).thenReturn(Optional.of(hospital));

        hospitalService.delete(HOSPITAL_ID);

        verify(hospitalRepository).delete(hospital);
    }

    @Test
    @DisplayName("delete - hôpital inexistant → ResourceNotFoundException")
    void delete_notFound_throws404() {
        when(hospitalRepository.findById(HOSPITAL_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> hospitalService.delete(HOSPITAL_ID))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("search - retourne les hôpitaux dont le nom contient le terme")
    void search_returnsMatchingHospitals() {
        Hospital hospital = buildHospital("CHU de Cotonou");
        HospitalResponseDto dto = buildResponseDto("CHU de Cotonou");

        when(hospitalRepository.findByNameContainingIgnoreCaseAndBranchId("CHU", BRANCH_ID)).thenReturn(List.of(hospital));
        when(hospitalMapper.toResponseDto(hospital)).thenReturn(dto);

        List<HospitalResponseDto> result = hospitalService.search("CHU", BRANCH_ID);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("CHU de Cotonou");
    }
}
