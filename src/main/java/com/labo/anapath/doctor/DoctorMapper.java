package com.labo.anapath.doctor;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper MapStruct pour la conversion entre l'entité {@link Doctor} et ses DTO.
 * <p>
 * Les champs techniques hérités de {@code AuditableEntity} sont exclus des
 * mappings d'écriture. Le {@code branchId} est assigné manuellement par le service
 * après la conversion, car il provient du contexte de sécurité et non du DTO.
 * </p>
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DoctorMapper {

    /**
     * Convertit une entité {@link Doctor} en DTO de réponse.
     *
     * @param doctor l'entité source
     * @return le DTO correspondant
     */
    DoctorResponseDto toResponseDto(Doctor doctor);

    /**
     * Convertit un DTO de requête en entité {@link Doctor}.
     * Le {@code branchId} doit être assigné manuellement après l'appel.
     *
     * @param dto le DTO source
     * @return l'entité prête à être persistée
     */
    Doctor toEntity(DoctorRequestDto dto);

    /**
     * Met à jour une entité {@link Doctor} existante à partir d'un DTO, en ignorant
     * les valeurs {@code null} (mise à jour partielle).
     *
     * @param dto    les nouvelles valeurs
     * @param doctor l'entité cible à modifier en place
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "branchId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateEntityFromDto(DoctorRequestDto dto, @MappingTarget Doctor doctor);
}
