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
class DoctorServiceImplTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private DoctorMapper doctorMapper;

    @InjectMocks
    private DoctorServiceImpl doctorService;

    private final UUID BRANCH_ID = UUID.randomUUID();
    private final UUID DOCTOR_ID = UUID.randomUUID();

    private Doctor buildDoctor(String name) {
        Doctor d = new Doctor();
        d.setName(name);
        d.setTelephone("+229 00000000");
        d.setBranchId(BRANCH_ID);
        return d;
    }

    private DoctorResponseDto buildResponseDto(String name) {
        return new DoctorResponseDto(DOCTOR_ID, name, "+229 00000000", null, null, null, BRANCH_ID, LocalDateTime.now());
    }

    @Test
    @DisplayName("create - crée un médecin et retourne le DTO")
    void create_success_returnsDto() {
        DoctorRequestDto dto = new DoctorRequestDto();
        dto.setName("Jean Dupont");

        Doctor doctor = buildDoctor("Jean Dupont");
        DoctorResponseDto responseDto = buildResponseDto("Jean Dupont");

        when(doctorRepository.existsByNameIgnoreCaseAndBranchId("Jean Dupont", BRANCH_ID)).thenReturn(false);
        when(doctorMapper.toEntity(dto)).thenReturn(doctor);
        when(doctorRepository.save(any(Doctor.class))).thenReturn(doctor);
        when(doctorMapper.toResponseDto(doctor)).thenReturn(responseDto);

        DoctorResponseDto result = doctorService.create(dto, BRANCH_ID);

        assertThat(result.name()).isEqualTo("Jean Dupont");
        verify(doctorRepository).save(doctor);
    }

    @Test
    @DisplayName("create - doublon nom → DuplicateResourceException")
    void create_duplicateName_throws409() {
        DoctorRequestDto dto = new DoctorRequestDto();
        dto.setName("Jean Dupont");

        when(doctorRepository.existsByNameIgnoreCaseAndBranchId("Jean Dupont", BRANCH_ID)).thenReturn(true);

        assertThatThrownBy(() -> doctorService.create(dto, BRANCH_ID))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    @DisplayName("findAll - retourne une page paginée")
    void findAll_returnsPaginatedResults() {
        Doctor doctor = buildDoctor("Jean Dupont");
        DoctorResponseDto dto = buildResponseDto("Jean Dupont");
        Page<Doctor> page = new PageImpl<>(List.of(doctor));

        when(doctorRepository.findByBranchId(any(UUID.class), any(Pageable.class))).thenReturn(page);
        when(doctorMapper.toResponseDto(doctor)).thenReturn(dto);

        PageResponse<DoctorResponseDto> result = doctorService.findAll(0, 20, BRANCH_ID);

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).name()).isEqualTo("Jean Dupont");
    }

    @Test
    @DisplayName("findById - retourne le DTO quand le médecin existe")
    void findById_found_returnsDto() {
        Doctor doctor = buildDoctor("Jean Dupont");
        DoctorResponseDto dto = buildResponseDto("Jean Dupont");

        when(doctorRepository.findById(DOCTOR_ID)).thenReturn(Optional.of(doctor));
        when(doctorMapper.toResponseDto(doctor)).thenReturn(dto);

        DoctorResponseDto result = doctorService.findById(DOCTOR_ID);

        assertThat(result.name()).isEqualTo("Jean Dupont");
    }

    @Test
    @DisplayName("findById - lève ResourceNotFoundException si inexistant")
    void findById_notFound_throws404() {
        when(doctorRepository.findById(DOCTOR_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> doctorService.findById(DOCTOR_ID))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("update - met à jour le médecin et retourne le DTO")
    void update_success_returnsUpdatedDto() {
        DoctorRequestDto dto = new DoctorRequestDto();
        dto.setName("Pierre Martin");

        Doctor doctor = buildDoctor("Jean Dupont");
        DoctorResponseDto responseDto = buildResponseDto("Pierre Martin");

        when(doctorRepository.findById(DOCTOR_ID)).thenReturn(Optional.of(doctor));
        when(doctorRepository.existsByNameIgnoreCaseAndBranchIdAndIdNot("Pierre Martin", BRANCH_ID, DOCTOR_ID)).thenReturn(false);
        when(doctorRepository.save(any(Doctor.class))).thenReturn(doctor);
        when(doctorMapper.toResponseDto(doctor)).thenReturn(responseDto);

        DoctorResponseDto result = doctorService.update(DOCTOR_ID, dto);

        assertThat(result.name()).isEqualTo("Pierre Martin");
        verify(doctorMapper).updateEntityFromDto(dto, doctor);
    }

    @Test
    @DisplayName("update - doublon nom sur un autre médecin → DuplicateResourceException")
    void update_duplicateName_throws409() {
        DoctorRequestDto dto = new DoctorRequestDto();
        dto.setName("Pierre Martin");

        Doctor doctor = buildDoctor("Jean Dupont");

        when(doctorRepository.findById(DOCTOR_ID)).thenReturn(Optional.of(doctor));
        when(doctorRepository.existsByNameIgnoreCaseAndBranchIdAndIdNot("Pierre Martin", BRANCH_ID, DOCTOR_ID)).thenReturn(true);

        assertThatThrownBy(() -> doctorService.update(DOCTOR_ID, dto))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    @DisplayName("delete - supprime le médecin")
    void delete_callsRepositoryDelete() {
        Doctor doctor = buildDoctor("Jean Dupont");

        when(doctorRepository.findById(DOCTOR_ID)).thenReturn(Optional.of(doctor));

        doctorService.delete(DOCTOR_ID);

        verify(doctorRepository).delete(doctor);
    }

    @Test
    @DisplayName("delete - médecin inexistant → ResourceNotFoundException")
    void delete_notFound_throws404() {
        when(doctorRepository.findById(DOCTOR_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> doctorService.delete(DOCTOR_ID))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("search - retourne les médecins dont le nom contient le terme")
    void search_returnsMatchingDoctors() {
        Doctor doctor = buildDoctor("Jean Dupont");
        DoctorResponseDto dto = buildResponseDto("Jean Dupont");

        when(doctorRepository.searchByNameAndBranchId("Jean", BRANCH_ID)).thenReturn(List.of(doctor));
        when(doctorMapper.toResponseDto(doctor)).thenReturn(dto);

        List<DoctorResponseDto> result = doctorService.search("Jean", BRANCH_ID);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Jean Dupont");
    }
}
