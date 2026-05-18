package com.labo.anapath.test;

import com.labo.anapath.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DataCodeServiceImplTest {

    @Mock
    private DataCodeRepository dataCodeRepository;

    @Mock
    private TestCatalogueMapper mapper;

    @InjectMocks
    private DataCodeServiceImpl dataCodeService;

    private final UUID BRANCH_ID = UUID.randomUUID();
    private final UUID ID = UUID.randomUUID();

    private DataCode buildEntity(String label) {
        DataCode dc = new DataCode();
        dc.setCode("DC-001");
        dc.setLabel(label);
        dc.setType("general");
        dc.setBranchId(BRANCH_ID);
        return dc;
    }

    private DataCodeResponseDto buildResponseDto(String label) {
        return new DataCodeResponseDto(ID, "DC-001", label, "general", BRANCH_ID, LocalDateTime.now());
    }

    @Test
    @DisplayName("create - crée un DataCode et retourne le DTO")
    void create_success_returnsDto() {
        DataCodeRequestDto dto = new DataCodeRequestDto();
        dto.setCode("DC-001");
        dto.setLabel("Code Test");
        dto.setType("general");

        DataCode entity = buildEntity("Code Test");
        DataCodeResponseDto responseDto = buildResponseDto("Code Test");

        when(mapper.toDataCodeEntity(dto)).thenReturn(entity);
        when(dataCodeRepository.save(any(DataCode.class))).thenReturn(entity);
        when(mapper.toDataCodeResponseDto(entity)).thenReturn(responseDto);

        DataCodeResponseDto result = dataCodeService.create(dto, BRANCH_ID);

        assertThat(result.label()).isEqualTo("Code Test");
        verify(dataCodeRepository).save(entity);
    }

    @Test
    @DisplayName("findById - retourne le DTO si trouvé")
    void findById_found_returnsDto() {
        DataCode entity = buildEntity("Code Test");
        DataCodeResponseDto responseDto = buildResponseDto("Code Test");

        when(dataCodeRepository.findById(ID)).thenReturn(Optional.of(entity));
        when(mapper.toDataCodeResponseDto(entity)).thenReturn(responseDto);

        DataCodeResponseDto result = dataCodeService.findById(ID);

        assertThat(result.label()).isEqualTo("Code Test");
    }

    @Test
    @DisplayName("findById - lève ResourceNotFoundException si inexistant")
    void findById_notFound_throws404() {
        when(dataCodeRepository.findById(ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> dataCodeService.findById(ID))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("delete - supprime le DataCode")
    void delete_callsRepositoryDelete() {
        DataCode entity = buildEntity("Code Test");

        when(dataCodeRepository.findById(ID)).thenReturn(Optional.of(entity));

        dataCodeService.delete(ID);

        verify(dataCodeRepository).delete(entity);
    }

    @Test
    @DisplayName("delete - DataCode inexistant → ResourceNotFoundException")
    void delete_notFound_throws404() {
        when(dataCodeRepository.findById(ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> dataCodeService.delete(ID))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
