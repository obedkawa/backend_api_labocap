package com.labo.anapath.inventory;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InventoryMapper {

    @Mapping(target = "supplierId", source = "supplier.id")
    @Mapping(target = "supplierName", source = "supplier.name")
    ArticleResponseDto toArticleResponseDto(Article article);

    @Mapping(target = "supplier", ignore = true)
    @Mapping(target = "quantity", ignore = true)
    Article toArticleEntity(ArticleRequestDto dto);

    @Mapping(target = "articleId", source = "article.id")
    @Mapping(target = "articleName", source = "article.name")
    MovementResponseDto toMovementResponseDto(Movement movement);
}
