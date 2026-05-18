package com.labo.anapath.support;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TicketCommentMapper {

    @Mapping(target = "ticketId", source = "ticket.id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", expression = "java(comment.getUser() != null ? comment.getUser().getFirstname() + \" \" + comment.getUser().getLastname() : null)")
    TicketCommentResponseDto toResponseDto(TicketComment comment);
}
