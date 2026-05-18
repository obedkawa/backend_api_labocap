package com.labo.anapath.role;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper MapStruct pour la conversion entre l'entité {@link Role} et ses DTOs.
 *
 * <p>Utilise {@link PermissionMapper} pour mapper les permissions imbriquées.
 * Les associations {@code permissions} et {@code users} sont toujours ignorées
 * lors de la conversion DTO → entité : elles sont gérées manuellement
 * dans le service pour garantir la cohérence des relations JPA.</p>
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {PermissionMapper.class})
public interface RoleMapper {

    /**
     * Convertit une entité {@link Role} en {@link RoleResponseDto}.
     *
     * @param role entité source
     * @return DTO de réponse avec les permissions incluses
     */
    RoleResponseDto toResponseDto(Role role);

    /**
     * Convertit un {@link RoleRequestDto} en entité {@link Role}.
     * Les permissions et les utilisateurs sont ignorés et doivent être
     * assignés séparément dans le service.
     *
     * @param dto DTO source
     * @return entité partiellement initialisée (sans slug ni associations)
     */
    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "users", ignore = true)
    Role toEntity(RoleRequestDto dto);

    /**
     * Met à jour une entité {@link Role} existante à partir d'un {@link RoleRequestDto}.
     * Les propriétés nulles du DTO sont ignorées (mise à jour partielle).
     * Les champs d'audit et les identifiants ne sont jamais modifiés.
     *
     * @param dto  DTO contenant les nouvelles valeurs
     * @param role entité cible à modifier
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "branchId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateEntityFromDto(RoleRequestDto dto, @MappingTarget Role role);
}
