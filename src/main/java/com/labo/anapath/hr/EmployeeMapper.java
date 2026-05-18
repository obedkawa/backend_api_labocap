package com.labo.anapath.hr;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper MapStruct pour la conversion entre l'entité {@link Employee}
 * et ses DTOs de requête/réponse.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EmployeeMapper {

    /**
     * Convertit une entité {@link Employee} en DTO de réponse.
     * L'identifiant du compte utilisateur associé est aplati dans le DTO.
     *
     * @param employee entité à convertir
     * @return DTO de réponse
     */
    @Mapping(target = "userId", source = "user.id")
    EmployeeResponseDto toResponseDto(Employee employee);

    /**
     * Convertit un DTO de requête en entité {@link Employee}.
     * L'association vers {@link com.labo.anapath.user.User} est ignorée car
     * elle est gérée manuellement dans la couche service.
     *
     * @param dto DTO de requête
     * @return entité Employee (sans lien User)
     */
    @Mapping(target = "user", ignore = true)
    Employee toEntity(EmployeeRequestDto dto);
}
