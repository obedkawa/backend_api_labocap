package com.labo.anapath.finance;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ExpenseCategoryRequestDto {

    @NotBlank
    private String name;

    private String description;
}
