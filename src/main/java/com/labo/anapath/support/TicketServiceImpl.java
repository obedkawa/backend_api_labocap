package com.labo.anapath.support;

import com.labo.anapath.common.dto.PageResponse;
import com.labo.anapath.common.exception.ResourceNotFoundException;
import com.labo.anapath.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Implémentation de {@link TicketService} gérant la logique métier
 * des tickets de support interne du laboratoire.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final TicketMapper ticketMapper;

    /**
     * {@inheritDoc}
     * Les tickets sont triés par date de création décroissante (plus récents en premier).
     */
    @Override
    @Transactional(readOnly = true)
    public PageResponse<TicketResponseDto> findAll(int page, int size, UUID branchId) {
        return PageResponse.of(ticketRepository.findByBranchId(branchId,
                PageRequest.of(page, size, Sort.by("createdAt").descending()))
                .map(ticketMapper::toResponseDto));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public TicketResponseDto findById(UUID id) {
        return ticketMapper.toResponseDto(ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", id)));
    }

    /**
     * {@inheritDoc}
     * Le statut initial est forcé à {@link TicketStatus#OPEN}.
     * Si aucune priorité n'est fournie, {@link TicketPriority#MEDIUM} est appliquée par défaut.
     */
    @Override
    @Transactional
    public TicketResponseDto create(TicketRequestDto dto, UUID userId, UUID branchId) {
        Ticket ticket = ticketMapper.toEntity(dto);
        ticket.setBranchId(branchId);
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setPriority(dto.getPriority() != null ? dto.getPriority() : TicketPriority.MEDIUM);
        ticket.setTicketCode(generateTicketCode());
        ticket.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", userId)));
        return ticketMapper.toResponseDto(ticketRepository.save(ticket));
    }

    /**
     * {@inheritDoc}
     * La priorité n'est mise à jour que si une valeur est explicitement fournie.
     */
    @Override
    @Transactional
    public TicketResponseDto update(UUID id, TicketRequestDto dto) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", id));
        ticket.setTitle(dto.getTitle());
        ticket.setDescription(dto.getDescription());
        if (dto.getPriority() != null) ticket.setPriority(dto.getPriority());
        return ticketMapper.toResponseDto(ticketRepository.save(ticket));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public TicketResponseDto updateStatus(UUID id, TicketStatus status) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", id));
        ticket.setStatus(status);
        return ticketMapper.toResponseDto(ticketRepository.save(ticket));
    }

    private String generateTicketCode() {
        String month = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "TKT-" + month + "-" + suffix;
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public void delete(UUID id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", id));
        ticketRepository.delete(ticket);
    }
}
