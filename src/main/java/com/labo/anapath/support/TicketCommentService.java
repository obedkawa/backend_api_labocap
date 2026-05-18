package com.labo.anapath.support;

import java.util.List;
import java.util.UUID;

public interface TicketCommentService {
    TicketCommentResponseDto create(UUID ticketId, TicketCommentRequestDto dto, UUID userId, UUID branchId);
    List<TicketCommentResponseDto> findByTicketId(UUID ticketId);
}
