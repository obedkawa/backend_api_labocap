package com.labo.anapath.client;

import com.labo.anapath.common.dto.PageResponse;
import com.labo.anapath.common.exception.BusinessException;
import com.labo.anapath.common.exception.DuplicateResourceException;
import com.labo.anapath.common.exception.ResourceNotFoundException;
import com.labo.anapath.common.exception.UnauthorizedException;
import com.labo.anapath.contract.ContratRepository;
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
class ClientServiceImplTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientMapper clientMapper;

    @Mock
    private ContratRepository contratRepository;

    @InjectMocks
    private ClientServiceImpl clientService;

    private final UUID BRANCH_ID = UUID.randomUUID();
    private final UUID CLIENT_ID = UUID.randomUUID();

    private Client buildClient(String name) {
        Client c = new Client();
        c.setName(name);
        c.setIfu("IFU-001");
        c.setContact("+229 00000000");
        c.setBranchId(BRANCH_ID);
        return c;
    }

    private ClientResponseDto buildResponseDto(String name) {
        return new ClientResponseDto(CLIENT_ID, "IFU-001", name, null, "+229 00000000", BRANCH_ID, LocalDateTime.now());
    }

    @Test
    @DisplayName("create - crée un client et retourne le DTO")
    void create_success_returnsDto() {
        ClientRequestDto dto = new ClientRequestDto();
        dto.setName("Clinique ABCD");
        dto.setIfu("IFU-001");

        Client client = buildClient("Clinique ABCD");
        ClientResponseDto responseDto = buildResponseDto("Clinique ABCD");

        when(clientRepository.existsByNameIgnoreCaseAndBranchId("Clinique ABCD", BRANCH_ID)).thenReturn(false);
        when(clientRepository.existsByIfu("IFU-001")).thenReturn(false);
        when(clientMapper.toEntity(dto)).thenReturn(client);
        when(clientRepository.save(any(Client.class))).thenReturn(client);
        when(clientMapper.toResponseDto(client)).thenReturn(responseDto);

        ClientResponseDto result = clientService.create(dto, BRANCH_ID);

        assertThat(result.name()).isEqualTo("Clinique ABCD");
        verify(clientRepository).save(client);
    }

    @Test
    @DisplayName("create - nom en doublon → DuplicateResourceException")
    void create_duplicateName_throws409() {
        ClientRequestDto dto = new ClientRequestDto();
        dto.setName("Clinique ABCD");

        when(clientRepository.existsByNameIgnoreCaseAndBranchId("Clinique ABCD", BRANCH_ID)).thenReturn(true);

        assertThatThrownBy(() -> clientService.create(dto, BRANCH_ID))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    @DisplayName("create - IFU en doublon → DuplicateResourceException")
    void create_duplicateIfu_throws409() {
        ClientRequestDto dto = new ClientRequestDto();
        dto.setName("Clinique ABCD");
        dto.setIfu("IFU-EXISTANT");

        when(clientRepository.existsByNameIgnoreCaseAndBranchId("Clinique ABCD", BRANCH_ID)).thenReturn(false);
        when(clientRepository.existsByIfu("IFU-EXISTANT")).thenReturn(true);

        assertThatThrownBy(() -> clientService.create(dto, BRANCH_ID))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    @DisplayName("findAll - retourne une page paginée")
    void findAll_returnsPaginatedResults() {
        Client client = buildClient("Clinique ABCD");
        ClientResponseDto dto = buildResponseDto("Clinique ABCD");
        Page<Client> page = new PageImpl<>(List.of(client));

        when(clientRepository.findByBranchId(any(UUID.class), any(Pageable.class))).thenReturn(page);
        when(clientMapper.toResponseDto(client)).thenReturn(dto);

        PageResponse<ClientResponseDto> result = clientService.findAll(0, 20, BRANCH_ID);

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).name()).isEqualTo("Clinique ABCD");
    }

    @Test
    @DisplayName("findById - retourne le DTO quand le client existe")
    void findById_found_returnsDto() {
        Client client = buildClient("Clinique ABCD");
        ClientResponseDto dto = buildResponseDto("Clinique ABCD");

        when(clientRepository.findById(CLIENT_ID)).thenReturn(Optional.of(client));
        when(clientMapper.toResponseDto(client)).thenReturn(dto);

        ClientResponseDto result = clientService.findById(CLIENT_ID);

        assertThat(result.name()).isEqualTo("Clinique ABCD");
    }

    @Test
    @DisplayName("findById - lève ResourceNotFoundException si inexistant")
    void findById_notFound_throws404() {
        when(clientRepository.findById(CLIENT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientService.findById(CLIENT_ID))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("update - met à jour le client et retourne le DTO")
    void update_success_returnsUpdatedDto() {
        ClientRequestDto dto = new ClientRequestDto();
        dto.setName("Nouveau Nom");
        dto.setIfu("IFU-NEW");

        Client client = buildClient("Clinique ABCD");
        ClientResponseDto responseDto = buildResponseDto("Nouveau Nom");

        when(clientRepository.findById(CLIENT_ID)).thenReturn(Optional.of(client));
        when(clientRepository.existsByNameIgnoreCaseAndBranchIdAndIdNot("Nouveau Nom", BRANCH_ID, CLIENT_ID)).thenReturn(false);
        when(clientRepository.existsByIfuAndIdNot("IFU-NEW", CLIENT_ID)).thenReturn(false);
        when(clientRepository.save(any(Client.class))).thenReturn(client);
        when(clientMapper.toResponseDto(client)).thenReturn(responseDto);

        ClientResponseDto result = clientService.update(CLIENT_ID, dto, BRANCH_ID);

        assertThat(result.name()).isEqualTo("Nouveau Nom");
        verify(clientMapper).updateEntityFromDto(dto, client);
    }

    @Test
    @DisplayName("update - doublon de nom sur un autre client → DuplicateResourceException")
    void update_duplicateName_throws409() {
        ClientRequestDto dto = new ClientRequestDto();
        dto.setName("Clinique Existante");

        Client client = buildClient("Clinique ABCD");

        when(clientRepository.findById(CLIENT_ID)).thenReturn(Optional.of(client));
        when(clientRepository.existsByNameIgnoreCaseAndBranchIdAndIdNot("Clinique Existante", BRANCH_ID, CLIENT_ID)).thenReturn(true);

        assertThatThrownBy(() -> clientService.update(CLIENT_ID, dto, BRANCH_ID))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    @DisplayName("update - IFU en doublon sur un autre client → DuplicateResourceException")
    void update_duplicateIfu_throws409() {
        ClientRequestDto dto = new ClientRequestDto();
        dto.setName("Clinique ABCD");
        dto.setIfu("IFU-AUTRE");

        Client client = buildClient("Clinique ABCD");

        when(clientRepository.findById(CLIENT_ID)).thenReturn(Optional.of(client));
        when(clientRepository.existsByNameIgnoreCaseAndBranchIdAndIdNot("Clinique ABCD", BRANCH_ID, CLIENT_ID)).thenReturn(false);
        when(clientRepository.existsByIfuAndIdNot("IFU-AUTRE", CLIENT_ID)).thenReturn(true);

        assertThatThrownBy(() -> clientService.update(CLIENT_ID, dto, BRANCH_ID))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    @DisplayName("update - client d'une autre branche → UnauthorizedException")
    void update_wrongBranch_throwsUnauthorized() {
        ClientRequestDto dto = new ClientRequestDto();
        dto.setName("Nouveau Nom");

        Client client = buildClient("Clinique ABCD");
        UUID otherBranchId = UUID.randomUUID();

        when(clientRepository.findById(CLIENT_ID)).thenReturn(Optional.of(client));

        assertThatThrownBy(() -> clientService.update(CLIENT_ID, dto, otherBranchId))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("n'appartient pas à votre branche");
    }

    @Test
    @DisplayName("delete - client avec contrats liés → BusinessException 422")
    void delete_withLinkedContrats_throws422() {
        when(contratRepository.existsByClientId(CLIENT_ID)).thenReturn(true);

        assertThatThrownBy(() -> clientService.delete(CLIENT_ID))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("contrats liés");
    }

    @Test
    @DisplayName("delete - client sans contrats liés → soft delete")
    void delete_noLinkedContrats_callsRepositoryDelete() {
        Client client = buildClient("Clinique ABCD");

        when(contratRepository.existsByClientId(CLIENT_ID)).thenReturn(false);
        when(clientRepository.findById(CLIENT_ID)).thenReturn(Optional.of(client));

        clientService.delete(CLIENT_ID);

        verify(clientRepository).delete(client);
    }

    @Test
    @DisplayName("delete - client inexistant → ResourceNotFoundException")
    void delete_notFound_throws404() {
        when(contratRepository.existsByClientId(CLIENT_ID)).thenReturn(false);
        when(clientRepository.findById(CLIENT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientService.delete(CLIENT_ID))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("search - retourne les clients dont le nom contient le terme")
    void search_returnsMatchingClients() {
        Client client = buildClient("Clinique ABCD");
        ClientResponseDto dto = buildResponseDto("Clinique ABCD");

        when(clientRepository.findByNameContainingIgnoreCaseAndBranchId("Clinique", BRANCH_ID)).thenReturn(List.of(client));
        when(clientMapper.toResponseDto(client)).thenReturn(dto);

        List<ClientResponseDto> result = clientService.search("Clinique", BRANCH_ID);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Clinique ABCD");
    }
}
