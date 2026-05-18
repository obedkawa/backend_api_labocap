package com.labo.anapath.finance;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BankRequestDto {

    @NotBlank
    private String name;

    @NotBlank
    private String accountNumber;

    private String description;
}
