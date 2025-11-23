package com.cc.be.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
public class Evaluacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "proyecto_id")
    @JsonBackReference
    private Proyecto proyecto;

    @ManyToOne
    @JoinColumn(name = "formato_id")
    @JsonBackReference
    private Formato formato;

    private Long evaluadorId;

    private EstadoEvaluacion estado;

    private LocalDateTime fechaAsignacion;
    private LocalDateTime fechaAceptacion;
    private LocalDateTime fechaFinalizacion;

    private Integer tiempoLimiteHoras;
    private Integer calificacionTotal;

    @OneToMany(mappedBy = "evaluacion", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ItemEvaluado> items = new ArrayList<>();
}
