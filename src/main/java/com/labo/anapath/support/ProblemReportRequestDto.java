package com.labo.anapath.support;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ProblemReportRequestDto {
    @NotNull
    private UUID testOrderId;
    private UUID problemCategoryId;
    private String description;
}
