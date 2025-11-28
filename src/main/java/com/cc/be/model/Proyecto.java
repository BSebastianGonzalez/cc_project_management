package com.cc.be.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
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
    private Long investigadorId;
    private String titulo;
    private String resumen;
    private String palabrasClave;
    private String objetivoGeneral;
    private String objetivoEspecifico;
    private String justificacion;
    private LocalDateTime fechaCreacion;

    private String nivelEstudio;
    private List<Long> lineasInvestigacionIds;

    @OneToMany(mappedBy = "proyecto", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Archivo> archivos;

    @OneToMany(mappedBy = "proyecto", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Evaluacion> evaluaciones = new ArrayList<>();
}
