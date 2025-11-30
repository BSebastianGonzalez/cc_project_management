package com.cc.be.controller;

import com.cc.be.dto.*;
import com.cc.be.model.EstadoEvaluacion;
import com.cc.be.model.Evaluacion;
import com.cc.be.service.EvaluacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evaluaciones")
@RequiredArgsConstructor
public class EvaluacionController {

    private final EvaluacionService evaluacionService;

    // Asignar evaluación
    @PostMapping("/asignar")
    public Evaluacion asignarEvaluacion(@RequestBody AsignarEvaluacionDTO dto) {
        return evaluacionService.asignarEvaluacion(
                dto.getProyectoId(),
                dto.getFormatoId(),
                dto.getEvaluadorId(),
                dto.getTiempoLimiteHoras(),
                dto.getCalificacionRequerida(),
                dto.getAdminId()
        );
    }

    // Aceptar evaluación
    @PutMapping("/{id}/aceptar")
    public Evaluacion aceptarEvaluacion(@PathVariable Long id) {
        return evaluacionService.aceptarEvaluacion(id);
    }

    // Rechazar evaluación
    @PutMapping("/{id}/rechazar")
    public Evaluacion rechazarEvaluacion(@PathVariable Long id) {
        return evaluacionService.rechazarEvaluacion(id);
    }

    // Calificar item, se manda id de la evaluacion
    @PostMapping("/{id}/items")
    public ItemEvaluadoResponseDTO calificarItem(@PathVariable Long id,
                                                 @RequestBody CalificarItemDTO dto) {
        return evaluacionService.calificarItem(id, dto);
    }

    // Finalizar evaluación
    @PutMapping("/{id}/finalizar")
    public Evaluacion finalizarEvaluacion(@PathVariable Long id) {
        return evaluacionService.finalizarEvaluacion(id);
    }

    // Listar por estado
    @GetMapping("/estado/{estado}")
    public List<Evaluacion> getEvaluacionesPorEstado(@PathVariable EstadoEvaluacion estado) {
        return evaluacionService.getEvaluacionesPorEstado(estado);
    }

    // Obtener evaluación por id
    @GetMapping("/{id}")
    public Evaluacion getEvaluacionPorId(@PathVariable Long id) {
        return evaluacionService.getEvaluacionPorId(id);
    }

    // Listar todas las evaluaciones
    @GetMapping
    public List<Evaluacion> getTodasEvaluaciones() {
        return evaluacionService.getTodasEvaluaciones();
    }

    @GetMapping("/proyecto/{proyectoId}")
    public ResponseEntity<List<Evaluacion>> obtenerPorProyecto(@PathVariable Long proyectoId) {
        return ResponseEntity.ok(evaluacionService.getEvaluacionesPorProyecto(proyectoId));
    }

    @PutMapping("/{evaluacionId}/editar")
    public ResponseEntity<?> editarEvaluacion(
            @PathVariable Long evaluacionId,
            @RequestBody List<EditarItemEvaluadoDTO> itemsEditados) {

        evaluacionService.editarEvaluacion(evaluacionId, itemsEditados);
        return ResponseEntity.ok("Evaluación editada correctamente");
    }

    // Validar evaluación: ahora recibe adminId como request param
    @PostMapping("/{id}/validar")
    public ResponseEntity<Evaluacion> validarEvaluacion(@PathVariable Long id, @RequestParam Long adminId) {
        Evaluacion evaluacionValidada = evaluacionService.validarEvaluacion(id, adminId);
        return ResponseEntity.ok(evaluacionValidada);
    }

    // Invalidar evaluación (dto contiene adminId)
    @PostMapping("/{id}/invalidar")
    public ResponseEntity<Evaluacion> invalidarEvaluacion(@PathVariable Long id, @RequestBody InvalidarEvaluacionDTO dto) {
        Evaluacion nueva = evaluacionService.invalidarEvaluacion(id, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }
}
