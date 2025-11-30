package com.cc.be.model;

import com.fasterxml.jackson.annotation.*;
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
    @JsonIgnoreProperties("evaluaciones")
    private Proyecto proyecto;

    @ManyToOne
    @JoinColumn(name = "formato_id")
    @JsonIgnoreProperties("evaluaciones")
    private Formato formato;

    private Long evaluadorId;

    private EstadoEvaluacion estado;

    private LocalDateTime fechaAsignacion;
    private LocalDateTime fechaAceptacion;
    private LocalDateTime fechaFinalizacion;

    private Integer tiempoLimiteHoras;
    private Integer calificacionTotal;
    private int calificacionRequerida;
    private boolean aprobada;
    private boolean validada;

    // Campos de invalidacion
    private boolean invalidada;
    private String motivoInvalidacion;
    private LocalDateTime fechaInvalidacion;

    // Referencia a la evaluación original
    @ManyToOne
    @JoinColumn(name = "evaluacion_original_id")
    @JsonIgnore
    private Evaluacion evaluacionOriginal;

    @OneToMany(mappedBy = "evaluacion", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ItemEvaluado> items = new ArrayList<>();
}
