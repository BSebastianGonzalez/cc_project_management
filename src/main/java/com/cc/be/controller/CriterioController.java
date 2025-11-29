package com.cc.be.controller;

import com.cc.be.dto.CriterioDTO;
import com.cc.be.model.Criterio;
import com.cc.be.service.CriterioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/criterios")
@RequiredArgsConstructor
public class CriterioController {

    private final CriterioService criterioService;

    @PostMapping
    public Criterio create(@RequestBody CriterioDTO dto) {
        return criterioService.create(dto);
    }

    @GetMapping
    public List<Criterio> getAll() {
        return criterioService.getAll();
    }

    @GetMapping("/{id}")
    public Criterio getOne(@PathVariable Long id) {
        return criterioService.getById(id);
    }

    @PutMapping("/{id}")
    public Criterio update(@PathVariable Long id, @RequestBody CriterioDTO dto) {
        return criterioService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        criterioService.delete(id);
    }
}

