package com.cc.be.repository;

import com.cc.be.model.EstadoEvaluacion;
import com.cc.be.model.Evaluacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface EvaluacionRepository extends JpaRepository<Evaluacion, Long> {
    List<Evaluacion> findByProyectoId(Long proyectoId);
    List<Evaluacion> findByEstado(EstadoEvaluacion estado);
}

