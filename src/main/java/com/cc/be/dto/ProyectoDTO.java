package com.cc.be.dto;

import com.cc.be.model.NivelEstudios;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProyectoDTO {
    private String titulo;
    private String resumen;
    private String palabrasClave;
    private String objetivoGeneral;
    private String objetivoEspecifico;
    private String justificacion;
    private NivelEstudios nivelEstudios;
    private List<Long> lineasInvestigacionIds = new ArrayList<>();
}
