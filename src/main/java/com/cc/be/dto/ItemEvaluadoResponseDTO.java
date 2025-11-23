package com.cc.be.dto;

import com.cc.be.model.ItemEvaluado;
import lombok.Data;

@Data
public class ItemEvaluadoResponseDTO {
    private Long id;
    private Long itemFormatoId;
    private int calificacion;
    private String observacion;

    public ItemEvaluadoResponseDTO(ItemEvaluado itemEvaluado) {
        this.id = itemEvaluado.getId();
        this.itemFormatoId = itemEvaluado.getItemFormatoId();
        this.calificacion = itemEvaluado.getCalificacion();
        this.observacion = itemEvaluado.getObservacion();
    }
}
