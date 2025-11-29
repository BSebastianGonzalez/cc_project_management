package com.cc.be.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemFormato {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String descripcion;
    private int peso;

    @ManyToOne
    @JoinColumn(name = "criterio_id")
    @JsonBackReference
    private Criterio criterio;

    @ManyToOne
    @JoinColumn(name = "formato_id")
    @JsonBackReference
    private Formato formato;

    @JsonProperty("criterioNombre")
    public String getCriterioNombre() {
        return criterio != null ? criterio.getNombre() : null;
    }
}
