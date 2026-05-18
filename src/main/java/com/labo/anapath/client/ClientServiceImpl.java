package com.labo.anapath.client;

import com.labo.anapath.common.dto.PageResponse;
import com.labo.anapath.common.exception.BusinessException;
import com.labo.anapath.common.exception.DuplicateResourceException;
import com.labo.anapath.common.exception.ResourceNotFoundException;
import com.labo.anapath.common.exception.UnauthorizedException;
import com.labo.anapath.contract.ContratRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Implémentation de {@link ClientService} portant la logique métier des clients institutionnels.
 * <p>
 * Les règles métier appliquées sont :
 * <ul>
 *   <li>Unicité du nom au sein d'une agence (insensible à la casse)</li>
 *   <li>Unicité de l'IFU toutes agences confondues (identifiant fiscal national)</li>
 *   <li>Vérification d'appartenance à l'agence lors d'une mise à jour</li>
 *   <li>Interdiction de suppression si des contrats sont liés au client</li>
 * </ul>
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    // Utilisé pour vérifier l'absence de contrats avant suppression
    private final ContratRepository contratRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ClientResponseDto> findAll(int page, int size, UUID branchId) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ClientResponseDto> result = clientRepository.findByBranchId(branchId, pageRequest)
                .map(clientMapper::toResponseDto);
        return PageResponse.of(result);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientResponseDto findById(UUID id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", id));
        return clientMapper.toResponseDto(client);
    }

    @Override
    @Transactional
    public ClientResponseDto create(ClientRequestDto dto, UUID branchId) {
        if (clientRepository.existsByNameIgnoreCaseAndBranchId(dto.getName(), branchId)) {
            throw new DuplicateResourceException("Un client avec le nom '" + dto.getName() + "' existe déjà.");
        }
        // L'IFU est un identifiant fiscal unique national : la vérification est globale
        if (dto.getIfu() != null && clientRepository.existsByIfu(dto.getIfu())) {
            throw new DuplicateResourceException("Un client avec l'IFU '" + dto.getIfu() + "' existe déjà.");
        }
        Client client = clientMapper.toEntity(dto);
        client.setBranchId(branchId);
        Client saved = clientRepository.save(client);
        log.info("Client créé: {}", saved.getId());
        return clientMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    public ClientResponseDto update(UUID id, ClientRequestDto dto, UUID branchId) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", id));
        // Empêcher la modification d'un client appartenant à une autre agence
        if (!client.getBranchId().equals(branchId)) {
            throw new UnauthorizedException("Ce client n'appartient pas à votre branche.");
        }
        if (clientRepository.existsByNameIgnoreCaseAndBranchIdAndIdNot(dto.getName(), client.getBranchId(), id)) {
            throw new DuplicateResourceException("Un client avec le nom '" + dto.getName() + "' existe déjà.");
        }
        if (dto.getIfu() != null && clientRepository.existsByIfuAndIdNot(dto.getIfu(), id)) {
            throw new DuplicateResourceException("Un client avec l'IFU '" + dto.getIfu() + "' existe déjà.");
        }
        clientMapper.updateEntityFromDto(dto, client);
        return clientMapper.toResponseDto(clientRepository.save(client));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        // Interdire la suppression si des contrats référencent ce client
        if (contratRepository.existsByClientId(id)) {
            throw new BusinessException("Le client a des contrats liés.");
        }
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", id));
        clientRepository.delete(client);
        log.info("Client supprimé (soft): {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientResponseDto> search(String q, UUID branchId) {
        return clientRepository.findByNameContainingIgnoreCaseAndBranchId(q, branchId)
                .stream()
                .map(clientMapper::toResponseDto)
                .toList();
    }
}
