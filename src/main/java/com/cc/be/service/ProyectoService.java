package com.cc.be.service;

import com.cc.be.dto.ProyectoDTO;
import com.cc.be.model.Proyecto;
import com.cc.be.repository.ProyectoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProyectoService {

    private final ProyectoRepository proyectoRepository;

    public Proyecto createProyecto(ProyectoDTO dto) {

        Proyecto proyecto = new Proyecto();

        proyecto.setTitulo(dto.getTitulo());
        proyecto.setResumen(dto.getResumen());
        proyecto.setPalabrasClave(dto.getPalabrasClave());
        proyecto.setObjetivoGeneral(dto.getObjetivoGeneral());
        proyecto.setObjetivoEspecifico(dto.getObjetivoEspecifico());
        proyecto.setJustificacion(dto.getJustificacion());

        proyecto.setNivelEstudios(dto.getNivelEstudios());

        // Guardar directamente los IDs
        if (dto.getLineasInvestigacionIds() != null) {
            proyecto.setLineasInvestigacionIds(dto.getLineasInvestigacionIds());
        }

        return proyectoRepository.save(proyecto);
    }

    public Proyecto getProyectoById(Long id) {
        return proyectoRepository.findById(id).orElse(null);
    }

    public List<Proyecto> getAllProyectos() {
        return proyectoRepository.findAll();
    }

    public Proyecto updateProyecto(Long id, ProyectoDTO dto) {

        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        proyecto.setTitulo(dto.getTitulo());
        proyecto.setResumen(dto.getResumen());
        proyecto.setPalabrasClave(dto.getPalabrasClave());
        proyecto.setObjetivoGeneral(dto.getObjetivoGeneral());
        proyecto.setObjetivoEspecifico(dto.getObjetivoEspecifico());
        proyecto.setJustificacion(dto.getJustificacion());

        proyecto.setNivelEstudios(dto.getNivelEstudios());

        // Actualizar IDs directamente
        if (dto.getLineasInvestigacionIds() != null) {
            proyecto.setLineasInvestigacionIds(dto.getLineasInvestigacionIds());
        }

        return proyectoRepository.save(proyecto);
    }

    public boolean deleteProyecto(Long id) {
        return proyectoRepository.findById(id).map(proyecto -> {
            proyectoRepository.delete(proyecto);
            return true;
        }).orElse(false);
    }
}
