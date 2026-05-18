package com.labo.anapath.setting;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper MapStruct pour la conversion entre l'entité {@link Setting}
 * et ses DTOs de requête/réponse.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SettingMapper {

    /**
     * Convertit une entité {@link Setting} en DTO de réponse.
     *
     * @param setting entité à convertir
     * @return DTO de réponse
     */
    SettingResponseDto toResponseDto(Setting setting);

    /**
     * Convertit un DTO de requête en entité {@link Setting}.
     *
     * @param dto DTO de requête
     * @return entité Setting
     */
    Setting toEntity(SettingRequestDto dto);
}
