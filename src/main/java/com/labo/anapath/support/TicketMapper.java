package com.labo.anapath.support;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper MapStruct pour la conversion entre l'entité {@link Ticket}
 * et ses DTOs de requête/réponse.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TicketMapper {

    /**
     * Convertit une entité {@link Ticket} en DTO de réponse.
     * Le nom complet de l'utilisateur est construit par expression Java
     * car il résulte de la concaténation de deux champs distincts.
     *
     * @param ticket entité à convertir
     * @return DTO de réponse du ticket
     */
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", expression = "java(ticket.getUser() != null ? ticket.getUser().getFirstname() + \" \" + ticket.getUser().getLastname() : null)")
    TicketResponseDto toResponseDto(Ticket ticket);

    /**
     * Convertit un DTO de requête en entité {@link Ticket}.
     * L'association vers {@link com.labo.anapath.user.User} est ignorée car
     * elle est résolue manuellement dans la couche service.
     *
     * @param dto DTO de requête
     * @return entité Ticket (sans lien utilisateur)
     */
    @Mapping(target = "user", ignore = true)
    Ticket toEntity(TicketRequestDto dto);
}
