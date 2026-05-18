package com.labo.anapath.setting;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SettingInvoiceRequestDto {
    private String ifu;
    private String token;
    private Boolean status;
}
