package com.cc.be.repository;

import com.cc.be.model.ItemFormato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemFormatoRepository extends JpaRepository<ItemFormato, Long> {
}
