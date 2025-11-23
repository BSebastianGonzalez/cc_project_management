package com.cc.be.dto;

import lombok.Data;

@Data
public class AsignarEvaluacionDTO {
    private Long proyectoId;
    private Long formatoId;
    private Long evaluadorId;
    private Integer tiempoLimiteHoras;
}
