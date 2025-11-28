package com.cc.be.repository;

import com.cc.be.model.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {
    List<Proyecto> findByInvestigadorId(Long investigadorId);
}
