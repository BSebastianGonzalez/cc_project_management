package com.cc.be.dto;

import lombok.Data;

@Data
public class EditarItemEvaluadoDTO {
    private Long itemEvaluadoId;   // ID del ItemEvaluado ya guardado
    private int calificacion;
    private String observacion;
}
