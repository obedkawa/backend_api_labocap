package com.labo.anapath.branch;

import com.labo.anapath.common.dto.PageResponse;
import com.labo.anapath.common.exception.BusinessException;
import com.labo.anapath.common.exception.DuplicateResourceException;
import com.labo.anapath.common.exception.ResourceNotFoundException;
import com.labo.anapath.user.UserRepository;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BranchServiceImplTest {

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BranchMapper branchMapper;

    @InjectMocks
    private BranchServiceImpl branchService;

    private final UUID BRANCH_ID = UUID.randomUUID();

    private Branch buildBranch(String name) {
        Branch b = new Branch();
        b.setName(name);
        b.setCode("CODE-001");
        b.setLocation("Cotonou");
        return b;
    }

    private BranchResponseDto buildResponseDto(String name) {
        return new BranchResponseDto(BRANCH_ID, name, "CODE-001", "Cotonou", LocalDateTime.now());
    }

    @Test
    @DisplayName("create - crée une agence et retourne le DTO")
    void create_success_returnsBranchDto() {
        BranchRequestDto dto = new BranchRequestDto();
        dto.setName("Agence Cotonou");

        Branch branch = buildBranch("Agence Cotonou");
        BranchResponseDto responseDto = buildResponseDto("Agence Cotonou");

        when(branchRepository.existsByNameIgnoreCase("Agence Cotonou")).thenReturn(false);
        when(branchMapper.toEntity(dto)).thenReturn(branch);
        when(branchRepository.save(any(Branch.class))).thenReturn(branch);
        when(branchMapper.toResponseDto(branch)).thenReturn(responseDto);

        BranchResponseDto result = branchService.create(dto);

        assertThat(result.name()).isEqualTo("Agence Cotonou");
        verify(branchRepository).save(branch);
    }

    @Test
    @DisplayName("create - nom en doublon → DuplicateResourceException")
    void create_duplicateName_throws409() {
        BranchRequestDto dto = new BranchRequestDto();
        dto.setName("Agence Cotonou");

        when(branchRepository.existsByNameIgnoreCase("Agence Cotonou")).thenReturn(true);

        assertThatThrownBy(() -> branchService.create(dto))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    @DisplayName("findAll - retourne une page paginée de toutes les agences")
    void findAll_returnsPaginatedResults() {
        Branch branch = buildBranch("Siège");
        BranchResponseDto dto = buildResponseDto("Siège");
        Page<Branch> page = new PageImpl<>(List.of(branch));

        when(branchRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(branchMapper.toResponseDto(branch)).thenReturn(dto);

        PageResponse<BranchResponseDto> result = branchService.findAll(0, 20);

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).name()).isEqualTo("Siège");
    }

    @Test
    @DisplayName("findById - retourne le DTO quand l'agence existe")
    void findById_found_returnsDto() {
        Branch branch = buildBranch("Siège");
        BranchResponseDto dto = buildResponseDto("Siège");

        when(branchRepository.findById(BRANCH_ID)).thenReturn(Optional.of(branch));
        when(branchMapper.toResponseDto(branch)).thenReturn(dto);

        BranchResponseDto result = branchService.findById(BRANCH_ID);

        assertThat(result.name()).isEqualTo("Siège");
    }

    @Test
    @DisplayName("findById - lève ResourceNotFoundException si inexistant")
    void findById_notFound_throws404() {
        when(branchRepository.findById(BRANCH_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> branchService.findById(BRANCH_ID))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("update - met à jour l'agence et retourne le DTO")
    void update_success_returnsUpdatedDto() {
        BranchRequestDto dto = new BranchRequestDto();
        dto.setName("Nouveau Nom");

        Branch branch = buildBranch("Ancien Nom");
        BranchResponseDto responseDto = buildResponseDto("Nouveau Nom");

        when(branchRepository.findById(BRANCH_ID)).thenReturn(Optional.of(branch));
        when(branchRepository.existsByNameIgnoreCaseAndIdNot("Nouveau Nom", BRANCH_ID)).thenReturn(false);
        when(branchRepository.save(any(Branch.class))).thenReturn(branch);
        when(branchMapper.toResponseDto(branch)).thenReturn(responseDto);

        BranchResponseDto result = branchService.update(BRANCH_ID, dto);

        assertThat(result.name()).isEqualTo("Nouveau Nom");
        verify(branchMapper).updateEntityFromDto(dto, branch);
    }

    @Test
    @DisplayName("update - doublon de nom sur une autre agence → DuplicateResourceException")
    void update_duplicateName_throws409() {
        BranchRequestDto dto = new BranchRequestDto();
        dto.setName("Nom Existant");

        Branch branch = buildBranch("Ancien Nom");

        when(branchRepository.findById(BRANCH_ID)).thenReturn(Optional.of(branch));
        when(branchRepository.existsByNameIgnoreCaseAndIdNot("Nom Existant", BRANCH_ID)).thenReturn(true);

        assertThatThrownBy(() -> branchService.update(BRANCH_ID, dto))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    @DisplayName("delete - branche avec utilisateurs liés → BusinessException 422")
    void delete_withLinkedUsers_throws422() {
        when(userRepository.existsByBranchId(BRANCH_ID)).thenReturn(true);

        assertThatThrownBy(() -> branchService.delete(BRANCH_ID))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("utilisateurs liés");
    }

    @Test
    @DisplayName("delete - branche sans utilisateurs liés → soft delete")
    void delete_noLinkedUsers_callsRepositoryDelete() {
        Branch branch = buildBranch("Agence Test");

        when(userRepository.existsByBranchId(BRANCH_ID)).thenReturn(false);
        when(branchRepository.findById(BRANCH_ID)).thenReturn(Optional.of(branch));

        branchService.delete(BRANCH_ID);

        verify(branchRepository).delete(branch);
    }

    @Test
    @DisplayName("delete - agence inexistante → ResourceNotFoundException")
    void delete_notFound_throws404() {
        when(userRepository.existsByBranchId(BRANCH_ID)).thenReturn(false);
        when(branchRepository.findById(BRANCH_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> branchService.delete(BRANCH_ID))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
