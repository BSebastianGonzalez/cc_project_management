package com.cc.be.service;

import com.cc.be.dto.CalificarItemDTO;
import com.cc.be.dto.EditarItemEvaluadoDTO;
import com.cc.be.dto.ItemEvaluadoResponseDTO;
import com.cc.be.model.*;
import com.cc.be.repository.EvaluacionRepository;
import com.cc.be.repository.FormatoRepository;
import com.cc.be.repository.ItemEvaluadoRepository;
import com.cc.be.repository.ProyectoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EvaluacionService {

    private final EvaluacionRepository evaluacionRepository;
    private final ItemEvaluadoRepository itemEvaluadoRepository;
    private final ProyectoRepository proyectoRepository;
    private final FormatoRepository formatoRepository;

    // 1. Crear/Asignar evaluación
    public Evaluacion asignarEvaluacion(Long proyectoId, Long formatoId, Long evaluadorId, Integer tiempoLimiteHoras, int calificacionRequerida) {
        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        // Verificar si existe alguna evaluación activa
        List<Evaluacion> evaluacionesExistentes = evaluacionRepository.findByProyectoId(proyectoId);
        boolean tieneActiva = evaluacionesExistentes.stream()
                .anyMatch(e -> e.getEstado() == EstadoEvaluacion.ASIGNADA || e.getEstado() == EstadoEvaluacion.ACEPTADA);

        if (tieneActiva) {
            throw new RuntimeException("Proyecto ya tiene una evaluación activa");
        }

        // Si quieres también puedes bloquear si ya está COMPLETADA
        boolean yaCompletada = evaluacionesExistentes.stream()
                .anyMatch(e -> e.getEstado() == EstadoEvaluacion.COMPLETADA);
        if (yaCompletada) {
            throw new RuntimeException("Proyecto ya ha sido evaluado y completado");
        }

        Formato formato = formatoRepository.findById(formatoId)
                .orElseThrow(() -> new RuntimeException("Formato no encontrado"));

        Evaluacion evaluacion = new Evaluacion();
        evaluacion.setProyecto(proyecto);
        evaluacion.setFormato(formato);
        evaluacion.setEvaluadorId(evaluadorId);
        evaluacion.setEstado(EstadoEvaluacion.ASIGNADA);
        evaluacion.setFechaAsignacion(LocalDateTime.now());
        evaluacion.setTiempoLimiteHoras(tiempoLimiteHoras);
        evaluacion.setCalificacionRequerida(calificacionRequerida);

        return evaluacionRepository.save(evaluacion);
    }



    // 2. Aceptar evaluación
    public Evaluacion aceptarEvaluacion(Long evaluacionId) {
        Evaluacion evaluacion = evaluacionRepository.findById(evaluacionId)
                .orElseThrow(() -> new RuntimeException("Evaluación no encontrada"));

        if (evaluacion.getEstado() != EstadoEvaluacion.ASIGNADA) {
            throw new RuntimeException("Evaluación no está en estado ASIGNADA");
        }

        LocalDateTime limite = evaluacion.getFechaAsignacion().plusHours(evaluacion.getTiempoLimiteHoras());
        if (LocalDateTime.now().isAfter(limite)) {
            evaluacion.setEstado(EstadoEvaluacion.RECHAZADA);
            return evaluacionRepository.save(evaluacion);
        }

        evaluacion.setEstado(EstadoEvaluacion.ACEPTADA);
        evaluacion.setFechaAceptacion(LocalDateTime.now());
        return evaluacionRepository.save(evaluacion);
    }

    // 3. Rechazar evaluación
    public Evaluacion rechazarEvaluacion(Long evaluacionId) {
        Evaluacion evaluacion = evaluacionRepository.findById(evaluacionId)
                .orElseThrow(() -> new RuntimeException("Evaluación no encontrada"));

        if (evaluacion.getEstado() != EstadoEvaluacion.ASIGNADA) {
            throw new RuntimeException("Evaluación no está en estado ASIGNADA");
        }

        evaluacion.setEstado(EstadoEvaluacion.RECHAZADA);
        return evaluacionRepository.save(evaluacion);
    }

    // 4. Calificar item
    public ItemEvaluadoResponseDTO calificarItem(Long evaluacionId, CalificarItemDTO dto) {
        // 1. Obtener evaluación
        Evaluacion evaluacion = evaluacionRepository.findById(evaluacionId)
                .orElseThrow(() -> new RuntimeException("Evaluación no encontrada"));

        // 2. Verificar estado
        if (evaluacion.getEstado() != EstadoEvaluacion.ACEPTADA) {
            throw new RuntimeException("Evaluación no aceptada, no se puede calificar");
        }

        // 3. Verificar que el item pertenece al formato
        ItemFormato itemFormato = evaluacion.getFormato().getItems().stream()
                .filter(item -> item.getId().equals(dto.getItemFormatoId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("El item no pertenece al formato asignado"));

        // 4. Validar calificación
        if (dto.getCalificacion() > itemFormato.getPeso()) {
            throw new RuntimeException("La calificación no puede superar el peso máximo del item (" + itemFormato.getPeso() + ")");
        }

        // 5. Buscar si ya existe calificación
        ItemEvaluado itemEvaluado = evaluacion.getItems().stream()
                .filter(i -> i.getItemFormatoId().equals(dto.getItemFormatoId()))
                .findFirst()
                .orElse(new ItemEvaluado());

        // 6. Setear valores
        itemEvaluado.setItemFormatoId(dto.getItemFormatoId());
        itemEvaluado.setCalificacion(dto.getCalificacion());
        itemEvaluado.setObservacion(dto.getObservacion());
        itemEvaluado.setEvaluacion(evaluacion);

        // 7. Agregar si es nuevo
        if (evaluacion.getItems().stream().noneMatch(i -> i.getItemFormatoId().equals(dto.getItemFormatoId()))) {
            evaluacion.getItems().add(itemEvaluado);
        }

        // 8. Guardar (cascada)
        evaluacionRepository.save(evaluacion);

        // 9. Retornar DTO limpio
        return new ItemEvaluadoResponseDTO(itemEvaluado);
    }



    // 5. Finalizar evaluación
    public Evaluacion finalizarEvaluacion(Long evaluacionId) {
        Evaluacion evaluacion = evaluacionRepository.findById(evaluacionId)
                .orElseThrow(() -> new RuntimeException("Evaluación no encontrada"));

        if (evaluacion.getEstado() != EstadoEvaluacion.ACEPTADA) {
            throw new RuntimeException("Evaluación no aceptada, no se puede finalizar");
        }

        // 1. Validar que todos los items del formato tengan calificación
        List<Long> idsItemsFormato = evaluacion.getFormato().getItems().stream()
                .map(ItemFormato::getId)
                .toList();

        List<Long> idsItemsCalificados = evaluacion.getItems().stream()
                .map(ItemEvaluado::getItemFormatoId)
                .toList();

        if (!idsItemsCalificados.containsAll(idsItemsFormato)) {
            throw new RuntimeException("No todos los items del formato han sido calificados");
        }

        // 2. Calcular calificación total sumando directamente las calificaciones de los items
        int calificacionTotal = evaluacion.getItems().stream()
                .mapToInt(ItemEvaluado::getCalificacion)
                .sum();

        evaluacion.setCalificacionTotal(calificacionTotal);

        evaluacion.setAprobada(calificacionTotal >= evaluacion.getCalificacionRequerida());

        // 3. Finalizar evaluación
        evaluacion.setEstado(EstadoEvaluacion.COMPLETADA);
        evaluacion.setFechaFinalizacion(LocalDateTime.now());

        return evaluacionRepository.save(evaluacion);
    }


    // 6. Consultar evaluaciones
    public List<Evaluacion> getEvaluacionesPorEstado(EstadoEvaluacion estado) {
        return evaluacionRepository.findByEstado(estado);
    }

    public List<Evaluacion> getTodasEvaluaciones() {
        return evaluacionRepository.findAll();
    }

    public Evaluacion getEvaluacionPorId(Long id) {
        return evaluacionRepository.findById(id).orElse(null);
    }

    public List<Evaluacion> getEvaluacionesPorProyecto(Long proyectoId) {
        return evaluacionRepository.findByProyectoId(proyectoId);
    }

    // 7. Editar evaluación para administrador
    public void editarEvaluacion(Long evaluacionId, List<EditarItemEvaluadoDTO> itemsEditados) {

        Evaluacion evaluacion = evaluacionRepository.findById(evaluacionId)
                .orElseThrow(() -> new RuntimeException("Evaluación no encontrada"));

        // 1. Editar cada item evaluado
        for (EditarItemEvaluadoDTO dto : itemsEditados) {

            ItemEvaluado item = itemEvaluadoRepository.findById(dto.getItemEvaluadoId())
                    .orElseThrow(() -> new RuntimeException("Item evaluado no encontrado"));

            // Seguridad: verificar que el item pertenece a esta evaluación
            if (!item.getEvaluacion().getId().equals(evaluacionId)) {
                throw new RuntimeException("El item evaluado no pertenece a la evaluación");
            }

            // Editar valores
            item.setCalificacion(dto.getCalificacion());
            item.setObservacion(dto.getObservacion());
            itemEvaluadoRepository.save(item);
        }

        // 2. Recalcular calificación total REAL
        int nuevaCalificacionTotal = evaluacion.getItems().stream()
                .mapToInt(ItemEvaluado::getCalificacion)
                .sum();

        evaluacion.setCalificacionTotal(nuevaCalificacionTotal);
        evaluacion.setAprobada(nuevaCalificacionTotal >= evaluacion.getCalificacionRequerida());

        evaluacionRepository.save(evaluacion);
    }

}

