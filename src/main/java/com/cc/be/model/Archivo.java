package com.cc.be.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Archivo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String urlArchivo;
    private String nombreArchivo;
    private String tipoMime;
    private String tipo;

    @ManyToOne
    @JoinColumn(name = "proyecto_id")
    @JsonBackReference
    private Proyecto proyecto;
}
