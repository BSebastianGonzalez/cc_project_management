package com.cc.be.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProyectoDTO {
    private String titulo;
    private String resumen;
    private String palabrasClave;
    private String objetivoGeneral;
    private String objetivoEspecifico;
    private String justificacion;
    private String nivelEstudio;
    private List<Long> lineasInvestigacionIds;
}
