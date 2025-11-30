package com.cc.be.dto;

import lombok.Data;

@Data
public class InvalidarEvaluacionDTO {
    private String motivo;
    private Long nuevoEvaluadorId; // opcional, si se proporciona se usará en la nueva evaluación
}
