package com.cc.be.repository;

import com.cc.be.model.ItemFormato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemFormatoRepository extends JpaRepository<ItemFormato, Long> {
    List<ItemFormato> findByFormatoId(Long formatoId);

    @Query("SELECT COALESCE(SUM(i.peso), 0) FROM ItemFormato i WHERE i.formato.id = :formatoId")
    Integer sumPesosByFormato(Long formatoId);
}
