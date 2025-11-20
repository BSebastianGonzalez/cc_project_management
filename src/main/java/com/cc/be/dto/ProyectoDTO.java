package com.cc.be.dto;

import lombok.Data;

@Data
public class ProyectoDTO {
    private String titulo;
    private String resumen;
    private String palabrasClave;
    private String objetivoGeneral;
    private String objetivoEspecifico;
    private String justificacion;
}
