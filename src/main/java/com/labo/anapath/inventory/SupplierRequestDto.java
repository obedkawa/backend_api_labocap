package com.labo.anapath.inventory;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class SupplierRequestDto {

    @NotBlank(message = "Le nom du fournisseur est obligatoire")
    private String name;

    private String phone;
    private String email;
    private String address;
    private String category;
    private UUID categoryId;
}
