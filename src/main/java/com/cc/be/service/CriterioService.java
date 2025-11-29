package com.cc.be.service;

import com.cc.be.dto.CriterioDTO;
import com.cc.be.model.Criterio;
import com.cc.be.repository.CriterioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CriterioService {

    private final CriterioRepository criterioRepository;

    public Criterio create(CriterioDTO dto) {
        Criterio c = new Criterio();
        c.setNombre(dto.getNombre());
        return criterioRepository.save(c);
    }

    public List<Criterio> getAll() {
        return criterioRepository.findAll();
    }

    public Criterio getById(Long id) {
        return criterioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Criterio no encontrado"));
    }

    public Criterio update(Long id, CriterioDTO dto) {
        return criterioRepository.findById(id).map(c -> {
            c.setNombre(dto.getNombre());
            return criterioRepository.save(c);
        }).orElseThrow(() -> new RuntimeException("Criterio no encontrado"));
    }

    public void delete(Long id) {
        criterioRepository.deleteById(id);
    }
}

