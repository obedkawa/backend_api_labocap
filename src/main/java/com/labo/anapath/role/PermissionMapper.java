package com.labo.anapath.role;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper MapStruct pour la conversion de {@link Permission} vers {@link PermissionResponseDto}.
 *
 * <p>Seule la direction entité → DTO est nécessaire, les permissions
 * n'étant pas créables via l'API.</p>
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PermissionMapper {

    /**
     * Convertit une entité {@link Permission} en {@link PermissionResponseDto}.
     *
     * @param permission entité source
     * @return DTO de réponse
     */
    PermissionResponseDto toResponseDto(Permission permission);
}
