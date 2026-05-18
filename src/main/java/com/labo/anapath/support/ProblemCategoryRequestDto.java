package com.labo.anapath.support;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProblemCategoryRequestDto {
    @NotBlank
    private String name;
}
