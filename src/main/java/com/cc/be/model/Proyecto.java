package com.cc.be.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Proyecto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    private String resumen;
    private String palabrasClave;
    private String objetivoGeneral;
    private String objetivoEspecifico;
    private String justificacion;
    private NivelEstudios nivelEstudios;

    @OneToMany(mappedBy = "proyecto", cascade = CascadeType.ALL)
    private List<Archivo> archivos;

    private List<Long> lineasInvestigacionIds = new ArrayList<>();

}