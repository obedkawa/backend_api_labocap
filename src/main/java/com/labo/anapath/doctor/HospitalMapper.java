package com.labo.anapath.doctor;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper MapStruct pour la conversion entre l'entité {@link Hospital} et ses DTO.
 * <p>
 * Les champs techniques hérités de {@code AuditableEntity} sont exclus des
 * mappings d'écriture. Le {@code branchId} est assigné manuellement par le service.
 * </p>
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HospitalMapper {

    /**
     * Convertit une entité {@link Hospital} en DTO de réponse.
     *
     * @param hospital l'entité source
     * @return le DTO correspondant
     */
    HospitalResponseDto toResponseDto(Hospital hospital);

    /**
     * Convertit un DTO de requête en entité {@link Hospital}.
     * Le {@code branchId} doit être assigné manuellement après l'appel.
     *
     * @param dto le DTO source
     * @return l'entité prête à être persistée
     */
    Hospital toEntity(HospitalRequestDto dto);

    /**
     * Met à jour une entité {@link Hospital} existante à partir d'un DTO, en ignorant
     * les valeurs {@code null} (mise à jour partielle).
     *
     * @param dto      les nouvelles valeurs
     * @param hospital l'entité cible à modifier en place
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "branchId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateEntityFromDto(HospitalRequestDto dto, @MappingTarget Hospital hospital);
}
