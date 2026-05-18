package com.labo.anapath.inventory;

import com.labo.anapath.common.dto.PageResponse;

public record ArticlePageResponseDto(
        PageResponse<ArticleResponseDto> articles,
        long outOfStockCount,
        long lowStockCount
) {}
