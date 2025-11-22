package com.cc.be.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
public class Formato {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String descripcion;
    private String institucion;
    private boolean activo;

    @OneToMany(mappedBy = "formato", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ItemFormato> items = new ArrayList<>();

    public int getPesoTotal() {
        return items.stream().mapToInt(ItemFormato::getPeso).sum();
    }
}
