package com.cc.be.service;

import com.cc.be.dto.ProyectoDTO;
import com.cc.be.model.Archivo;
import com.cc.be.model.Proyecto;
import com.cc.be.repository.ProyectoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProyectoService {

    private final ProyectoRepository proyectoRepository;

    public Proyecto createProyecto(ProyectoDTO dto) {
        Proyecto proyecto = new Proyecto();
        proyecto.setInvestigadorId(dto.getInvestigadorId());
        proyecto.setTitulo(dto.getTitulo());
        proyecto.setResumen(dto.getResumen());
        proyecto.setPalabrasClave(dto.getPalabrasClave());
        proyecto.setObjetivoGeneral(dto.getObjetivoGeneral());
        proyecto.setObjetivoEspecifico(dto.getObjetivoEspecifico());
        proyecto.setJustificacion(dto.getJustificacion());

        proyecto.setNivelEstudio(dto.getNivelEstudio());
        proyecto.setLineasInvestigacionIds(dto.getLineasInvestigacionIds());

        proyecto.setFechaCreacion(LocalDateTime.now());

        return proyectoRepository.save(proyecto);
    }


    public Proyecto getProyectoById(Long id) {
        return proyectoRepository.findById(id).orElse(null);
    }

    public List<Proyecto> getAllProyectos() {
        return proyectoRepository.findAll();
    }

    public Proyecto updateProyecto(Long id, ProyectoDTO dto) {
        return proyectoRepository.findById(id).map(proyecto -> {

            proyecto.setInvestigadorId(dto.getInvestigadorId());
            proyecto.setTitulo(dto.getTitulo());
            proyecto.setResumen(dto.getResumen());
            proyecto.setPalabrasClave(dto.getPalabrasClave());
            proyecto.setObjetivoGeneral(dto.getObjetivoGeneral());
            proyecto.setObjetivoEspecifico(dto.getObjetivoEspecifico());
            proyecto.setJustificacion(dto.getJustificacion());

            proyecto.setNivelEstudio(dto.getNivelEstudio());
            proyecto.setLineasInvestigacionIds(dto.getLineasInvestigacionIds());

            return proyectoRepository.save(proyecto);

        }).orElse(null);
    }

    public boolean deleteProyecto(Long id) {
        return proyectoRepository.findById(id).map(proyecto -> {
            proyectoRepository.delete(proyecto);
            return true;
        }).orElse(false);
    }

    public List<Archivo> getArchivosByProyectoId(Long id) {
        return proyectoRepository.findById(id)
                .map(Proyecto::getArchivos)
                .orElse(Collections.emptyList());
    }

    public List<Proyecto> getProyectosByInvestigadorId(Long investigadorId) {
        return proyectoRepository.findByInvestigadorId(investigadorId);
    }
}
