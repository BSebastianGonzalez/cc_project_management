package com.cc.be.dto;

import lombok.Data;

import java.util.List;

@Data
public class FormatoDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private String institucion;
    private boolean activo;
    private List<ItemFormatoDTO> items;
}
