package com.cc.be.service;

import com.cc.be.dto.ProyectoDTO;
import com.cc.be.model.Proyecto;
import com.cc.be.repository.ProyectoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
            proyecto.setTitulo(dto.getTitulo());
            proyecto.setResumen(dto.getResumen());
            proyecto.setPalabrasClave(dto.getPalabrasClave());
            proyecto.setObjetivoGeneral(dto.getObjetivoGeneral());
            proyecto.setObjetivoEspecifico(dto.getObjetivoEspecifico());
            proyecto.setJustificacion(dto.getJustificacion());
            return proyectoRepository.save(proyecto);
        }).orElse(null);
    }

    public boolean deleteProyecto(Long id) {
        return proyectoRepository.findById(id).map(proyecto -> {
            proyectoRepository.delete(proyecto);
            return true;
        }).orElse(false);
    }
}
