package com.labo.anapath.doc;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentationCategoryRequestDto {
    @NotBlank
    private String name;
}
