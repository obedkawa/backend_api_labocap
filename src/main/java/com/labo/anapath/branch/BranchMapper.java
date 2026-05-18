package com.labo.anapath.branch;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper MapStruct pour la conversion entre l'entité {@link Branch} et ses DTO.
 * <p>
 * Géré comme un bean Spring ({@code componentModel = "spring"}).
 * Les champs techniques (id, timestamps) sont systématiquement exclus des mappings
 * d'écriture pour éviter tout écrasement involontaire.
 * </p>
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BranchMapper {

    /**
     * Convertit une entité {@link Branch} en DTO de réponse.
     *
     * @param branch l'entité source
     * @return le DTO correspondant
     */
    BranchResponseDto toResponseDto(Branch branch);

    /**
     * Convertit un DTO de requête en entité {@link Branch} (sans les champs techniques).
     *
     * @param dto le DTO source
     * @return l'entité prête à être persistée
     */
    Branch toEntity(BranchRequestDto dto);

    /**
     * Met à jour une entité {@link Branch} existante à partir d'un DTO, en ignorant
     * les valeurs {@code null} (mise à jour partielle).
     * Les champs techniques (id, timestamps) ne sont jamais modifiés.
     *
     * @param dto    les nouvelles valeurs
     * @param branch l'entité cible à modifier en place
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateEntityFromDto(BranchRequestDto dto, @MappingTarget Branch branch);
}
