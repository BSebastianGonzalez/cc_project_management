package com.cc.be.repository;

import com.cc.be.model.ItemEvaluado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemEvaluadoRepository extends JpaRepository<ItemEvaluado, Long> {
    List<ItemEvaluado> findByEvaluacionId(Long evaluacionId);
}
