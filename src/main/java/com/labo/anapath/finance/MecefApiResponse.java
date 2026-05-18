package com.labo.anapath.finance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

/** DTO de réponse de l'API MECeF impots.bj. */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MecefApiResponse {
    private String codeMECeFDGI;
    private String counters;
    private String dateTime;
    private String nim;
    private String qrCode;
}
