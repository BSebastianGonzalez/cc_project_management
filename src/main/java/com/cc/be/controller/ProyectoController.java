package com.cc.be.controller;

import com.cc.be.dto.ProyectoDTO;
import com.cc.be.model.Archivo;
import com.cc.be.model.Proyecto;
import com.cc.be.service.ProyectoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proyectos")
@RequiredArgsConstructor
public class ProyectoController {

    private final ProyectoService proyectoService;

    @PostMapping
    public Proyecto create(@RequestBody ProyectoDTO dto) {
        return proyectoService.createProyecto(dto);
    }

    @GetMapping("/{id}")
    public Proyecto getById(@PathVariable Long id) {
        return proyectoService.getProyectoById(id);
    }

    @GetMapping
    public List<Proyecto> getAll() {
        return proyectoService.getAllProyectos();
    }

    @PutMapping("/{id}")
    public Proyecto update(@PathVariable Long id, @RequestBody ProyectoDTO dto) {
        return proyectoService.updateProyecto(id, dto);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return proyectoService.deleteProyecto(id);
    }


    @GetMapping("/{id}/archivos")
    public List<Archivo> getArchivos(@PathVariable Long id) {
        return proyectoService.getArchivosByProyectoId(id);
    }

    @GetMapping("/investigador/{investigadorId}")
    public List<Proyecto> getByInvestigadorId(@PathVariable Long investigadorId) {
        return proyectoService.getProyectosByInvestigadorId(investigadorId);
    }
}