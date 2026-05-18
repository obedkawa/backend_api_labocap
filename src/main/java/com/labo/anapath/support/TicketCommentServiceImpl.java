package com.labo.anapath.support;

import com.labo.anapath.common.exception.ResourceNotFoundException;
import com.labo.anapath.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketCommentServiceImpl implements TicketCommentService {

    private final TicketCommentRepository ticketCommentRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final TicketCommentMapper ticketCommentMapper;

    @Override
    @Transactional
    public TicketCommentResponseDto create(UUID ticketId, TicketCommentRequestDto dto, UUID userId, UUID branchId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", ticketId));
        TicketComment comment = new TicketComment();
        comment.setBranchId(branchId);
        comment.setTicket(ticket);
        comment.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", userId)));
        comment.setContent(dto.getContent());
        return ticketCommentMapper.toResponseDto(ticketCommentRepository.save(comment));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketCommentResponseDto> findByTicketId(UUID ticketId) {
        return ticketCommentRepository.findByTicketIdOrderByCreatedAtAsc(ticketId)
                .stream().map(ticketCommentMapper::toResponseDto).toList();
    }
}
