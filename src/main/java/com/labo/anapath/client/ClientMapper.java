package com.labo.anapath.client;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper MapStruct pour la conversion entre l'entité {@link Client} et ses DTO.
 * <p>
 * Les champs techniques hérités de {@code AuditableEntity} (id, branchId, timestamps,
 * auteurs de modification) sont systématiquement exclus des opérations d'écriture :
 * ils sont gérés par le service ou par l'infrastructure d'audit JPA.
 * </p>
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClientMapper {

    /**
     * Convertit une entité {@link Client} en DTO de réponse.
     *
     * @param client l'entité source
     * @return le DTO correspondant
     */
    ClientResponseDto toResponseDto(Client client);

    /**
     * Convertit un DTO de requête en entité {@link Client} (sans les champs techniques).
     * Le {@code branchId} doit être assigné manuellement après l'appel.
     *
     * @param dto le DTO source
     * @return l'entité prête à être persistée
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "branchId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Client toEntity(ClientRequestDto dto);

    /**
     * Met à jour une entité {@link Client} existante à partir d'un DTO, en ignorant
     * les valeurs {@code null} (mise à jour partielle).
     * Les champs techniques ne sont jamais modifiés.
     *
     * @param dto    les nouvelles valeurs
     * @param client l'entité cible à modifier en place
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "branchId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateEntityFromDto(ClientRequestDto dto, @MappingTarget Client client);
}
