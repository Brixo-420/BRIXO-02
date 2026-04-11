package com.brixo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PersonalDto {

    @JsonProperty("rol")
    private String rol;

    @JsonProperty("horas_estimadas")
    private Object horasEstimadas;   // puede llegar como number o string
}
