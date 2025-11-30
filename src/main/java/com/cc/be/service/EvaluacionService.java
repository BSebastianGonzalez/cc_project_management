package com.cc.be.service;

import com.cc.be.dto.CalificarItemDTO;
import com.cc.be.dto.EditarItemEvaluadoDTO;
import com.cc.be.dto.InvalidarEvaluacionDTO;
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

@Service
@RequiredArgsConstructor
public class EvaluacionService {

    private final EvaluacionRepository evaluacionRepository;
    private final ItemEvaluadoRepository itemEvaluadoRepository;
    private final ProyectoRepository proyectoRepository;
    private final FormatoRepository formatoRepository;
    private final NotificacionService notificacionService;

    // 1. Crear/Asignar evaluación (ahora recibe adminId)
    public Evaluacion asignarEvaluacion(Long proyectoId, Long formatoId, Long evaluadorId, Integer tiempoLimiteHoras, int calificacionRequerida, Long adminId) {
        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        List<Evaluacion> evaluacionesExistentes = evaluacionRepository.findByProyectoId(proyectoId);
        boolean tieneActiva = evaluacionesExistentes.stream()
                .anyMatch(e -> e.getEstado() == EstadoEvaluacion.ASIGNADA || e.getEstado() == EstadoEvaluacion.ACEPTADA);

        if (tieneActiva) {
            throw new RuntimeException("Proyecto ya tiene una evaluación activa");
        }

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
        evaluacion.setValidada(false);
        evaluacion.setAsignadorAdminId(adminId);

        Evaluacion saved = evaluacionRepository.save(evaluacion);

        // Notificar al evaluador con remitente = adminId
        if (evaluadorId != null) {
            String mensaje = "Se te asignó la evaluación (id: " + saved.getId() + ") del proyecto (id: " + proyecto.getId() + ").";
            notificacionService.crearNotificacion(evaluadorId, "Nueva evaluación asignada", mensaje, "EVALUACION_ASIGNADA", adminId);
        }

        return saved;
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
            Evaluacion saved = evaluacionRepository.save(evaluacion);
            // Notificar al admin asignador sobre rechazo por timeout
            Long adminId = saved.getAsignadorAdminId();
            if (adminId != null) {
                String msg = "La evaluación (id: " + saved.getId() + ") del proyecto (id: " + saved.getProyecto().getId() + ") fue rechazada por timeout.";
                notificacionService.crearNotificacion(adminId, "Evaluación rechazada (timeout)", msg, "EVALUACION_ESTADO", saved.getEvaluadorId());
            }
            return saved;
        }

        evaluacion.setEstado(EstadoEvaluacion.ACEPTADA);
        evaluacion.setFechaAceptacion(LocalDateTime.now());
        Evaluacion saved = evaluacionRepository.save(evaluacion);

        // Notificar al admin asignador
        Long adminId = saved.getAsignadorAdminId();
        if (adminId != null) {
            String msg = "La evaluación (id: " + saved.getId() + ") del proyecto (id: " + saved.getProyecto().getId() + ") fue aceptada por el evaluador.";
            notificacionService.crearNotificacion(adminId, "Evaluación aceptada", msg, "EVALUACION_ESTADO", saved.getEvaluadorId());
        }

        return saved;
    }

    // 3. Rechazar evaluación
    public Evaluacion rechazarEvaluacion(Long evaluacionId) {
        Evaluacion evaluacion = evaluacionRepository.findById(evaluacionId)
                .orElseThrow(() -> new RuntimeException("Evaluación no encontrada"));

        if (evaluacion.getEstado() != EstadoEvaluacion.ASIGNADA) {
            throw new RuntimeException("Evaluación no está en estado ASIGNADA");
        }

        evaluacion.setEstado(EstadoEvaluacion.RECHAZADA);
        Evaluacion saved = evaluacionRepository.save(evaluacion);

        // Notificar al admin asignador
        Long adminId = saved.getAsignadorAdminId();
        if (adminId != null) {
            String msg = "La evaluación (id: " + saved.getId() + ") del proyecto (id: " + saved.getProyecto().getId() + ") fue rechazada por el evaluador.";
            notificacionService.crearNotificacion(adminId, "Evaluación rechazada", msg, "EVALUACION_ESTADO", saved.getEvaluadorId());
        }

        return saved;
    }

    // 4. Calificar item (sin cambios en notificaciones)
    public ItemEvaluadoResponseDTO calificarItem(Long evaluacionId, CalificarItemDTO dto) {
        Evaluacion evaluacion = evaluacionRepository.findById(evaluacionId)
                .orElseThrow(() -> new RuntimeException("Evaluación no encontrada"));

        if (evaluacion.getEstado() != EstadoEvaluacion.ACEPTADA) {
            throw new RuntimeException("Evaluación no aceptada, no se puede calificar");
        }

        ItemFormato itemFormato = evaluacion.getFormato().getItems().stream()
                .filter(item -> item.getId().equals(dto.getItemFormatoId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("El item no pertenece al formato asignado"));

        if (dto.getCalificacion() > itemFormato.getPeso()) {
            throw new RuntimeException("La calificación no puede superar el peso máximo del item (" + itemFormato.getPeso() + ")");
        }

        ItemEvaluado itemEvaluado = evaluacion.getItems().stream()
                .filter(i -> i.getItemFormatoId().equals(dto.getItemFormatoId()))
                .findFirst()
                .orElse(new ItemEvaluado());

        itemEvaluado.setItemFormatoId(dto.getItemFormatoId());
        itemEvaluado.setCalificacion(dto.getCalificacion());
        itemEvaluado.setObservacion(dto.getObservacion());
        itemEvaluado.setEvaluacion(evaluacion);

        if (evaluacion.getItems().stream().noneMatch(i -> i.getItemFormatoId().equals(dto.getItemFormatoId()))) {
            evaluacion.getItems().add(itemEvaluado);
        }

        evaluacionRepository.save(evaluacion);

        return new ItemEvaluadoResponseDTO(itemEvaluado);
    }

    // 5. Finalizar evaluación
    public Evaluacion finalizarEvaluacion(Long evaluacionId) {
        Evaluacion evaluacion = evaluacionRepository.findById(evaluacionId)
                .orElseThrow(() -> new RuntimeException("Evaluación no encontrada"));

        if (evaluacion.getEstado() != EstadoEvaluacion.ACEPTADA) {
            throw new RuntimeException("Evaluación no aceptada, no se puede finalizar");
        }

        List<Long> idsItemsFormato = evaluacion.getFormato().getItems().stream()
                .map(ItemFormato::getId)
                .toList();

        List<Long> idsItemsCalificados = evaluacion.getItems().stream()
                .map(ItemEvaluado::getItemFormatoId)
                .toList();

        if (!idsItemsCalificados.containsAll(idsItemsFormato)) {
            throw new RuntimeException("No todos los items del formato han sido calificados");
        }

        int calificacionTotal = evaluacion.getItems().stream()
                .mapToInt(ItemEvaluado::getCalificacion)
                .sum();

        evaluacion.setCalificacionTotal(calificacionTotal);
        evaluacion.setAprobada(calificacionTotal >= evaluacion.getCalificacionRequerida());
        evaluacion.setEstado(EstadoEvaluacion.COMPLETADA);
        evaluacion.setFechaFinalizacion(LocalDateTime.now());

        Evaluacion saved = evaluacionRepository.save(evaluacion);

        // Notificar al admin asignador
        Long adminId = saved.getAsignadorAdminId();
        if (adminId != null) {
            String msg = "La evaluación (id: " + saved.getId() + ") del proyecto (id: " + saved.getProyecto().getId() + ") fue finalizada por el evaluador.";
            notificacionService.crearNotificacion(adminId, "Evaluación finalizada", msg, "EVALUACION_FINALIZADA", saved.getEvaluadorId());
        }

        return saved;
    }

    // Consultas y edición se mantienen (sin cambios significativos)
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

    public void editarEvaluacion(Long evaluacionId, List<EditarItemEvaluadoDTO> itemsEditados) {

        Evaluacion evaluacion = evaluacionRepository.findById(evaluacionId)
                .orElseThrow(() -> new RuntimeException("Evaluación no encontrada"));

        for (EditarItemEvaluadoDTO dto : itemsEditados) {

            ItemEvaluado item = itemEvaluadoRepository.findById(dto.getItemEvaluadoId())
                    .orElseThrow(() -> new RuntimeException("Item evaluado no encontrado"));

            if (!item.getEvaluacion().getId().equals(evaluacionId)) {
                throw new RuntimeException("El item evaluado no pertenece a la evaluación");
            }

            item.setCalificacion(dto.getCalificacion());
            item.setObservacion(dto.getObservacion());
            itemEvaluadoRepository.save(item);
        }

        int nuevaCalificacionTotal = evaluacion.getItems().stream()
                .mapToInt(ItemEvaluado::getCalificacion)
                .sum();

        evaluacion.setCalificacionTotal(nuevaCalificacionTotal);
        evaluacion.setAprobada(nuevaCalificacionTotal >= evaluacion.getCalificacionRequerida());

        evaluacionRepository.save(evaluacion);
    }

    // Validar evaluación (ahora recibe adminId para usar como remitente)
    public Evaluacion validarEvaluacion(Long evaluacionId, Long adminId) {
        Evaluacion evaluacion = evaluacionRepository.findById(evaluacionId)
                .orElseThrow(() -> new RuntimeException("Evaluación no encontrada"));

        if (evaluacion.getEstado() != EstadoEvaluacion.COMPLETADA) {
            throw new RuntimeException("Sólo se puede validar una evaluación COMPLETADA");
        }

        if (evaluacion.isValidada()) {
            throw new RuntimeException("Evaluación ya validada");
        }

        evaluacion.setValidada(true);
        Evaluacion saved = evaluacionRepository.save(evaluacion);

        Long evaluador = saved.getEvaluadorId();
        if (evaluador != null) {
            String mensaje = "La evaluación (id: " + saved.getId() + ") fue validada. Se ha habilitado para que suba los documentos relacionados con el pago.";
            notificacionService.crearNotificacion(evaluador, "Evaluación validada", mensaje, "EVALUACION_VALIDADA", adminId);
        }

        return saved;
    }

    // Invalidar evaluación (usa dto.adminId como remitente y asignador en la nueva evaluación)
    public Evaluacion invalidarEvaluacion(Long evaluacionId, InvalidarEvaluacionDTO dto) {
        Evaluacion evaluacion = evaluacionRepository.findById(evaluacionId)
                .orElseThrow(() -> new RuntimeException("Evaluación no encontrada"));

        if (evaluacion.isInvalidada()) {
            throw new RuntimeException("Evaluación ya invalidada");
        }

        if (evaluacion.getEstado() != EstadoEvaluacion.COMPLETADA) {
            throw new RuntimeException("Solo se puede invalidar una evaluación COMPLETADA");
        }

        // Marcar como invalidada y guardar motivo/fecha
        evaluacion.setInvalidada(true);
        evaluacion.setMotivoInvalidacion(dto.getMotivo());
        evaluacion.setFechaInvalidacion(LocalDateTime.now());
        evaluacionRepository.save(evaluacion);

        // Crear nueva evaluación para re-evaluación
        Evaluacion nueva = new Evaluacion();
        nueva.setProyecto(evaluacion.getProyecto());
        nueva.setFormato(evaluacion.getFormato());
        Long asignado = dto.getNuevoEvaluadorId() != null ? dto.getNuevoEvaluadorId() : evaluacion.getEvaluadorId();
        nueva.setEvaluadorId(asignado);
        nueva.setEstado(EstadoEvaluacion.ASIGNADA);
        nueva.setFechaAsignacion(LocalDateTime.now());
        nueva.setTiempoLimiteHoras(evaluacion.getTiempoLimiteHoras());
        nueva.setCalificacionRequerida(evaluacion.getCalificacionRequerida());
        nueva.setValidada(false);
        nueva.setInvalidada(false);
        nueva.setEvaluacionOriginal(evaluacion);
        nueva.setAsignadorAdminId(dto.getAdminId());

        Evaluacion savedNueva = evaluacionRepository.save(nueva);

        // Notificar al evaluador original sobre invalidación (remitente = adminId)
        Long evaluadorOriginal = evaluacion.getEvaluadorId();
        if (evaluadorOriginal != null) {
            String mensaje = "Tu evaluación (id: " + evaluacion.getId() + ") fue invalidada por el administrador. Motivo: " + dto.getMotivo();
            notificacionService.crearNotificacion(evaluadorOriginal, "Evaluación invalidada", mensaje, "EVALUACION_INVALIDADA", dto.getAdminId());
        }

        // Notificar al evaluador asignado para la re-evaluación
        if (asignado != null) {
            String mensajeAsignado;
            if (!asignado.equals(evaluadorOriginal)) {
                mensajeAsignado = "Se te asignó una re-evaluación (id: " + savedNueva.getId() + ") por invalidación (motivo: " + dto.getMotivo() + "). Eres el evaluador designado.";
            } else {
                mensajeAsignado = "Se te asignó nuevamente la evaluación (id: " + savedNueva.getId() + ") por invalidación (motivo: " + dto.getMotivo() + "). Debes realizarla de nuevo.";
            }
            notificacionService.crearNotificacion(asignado, "Re-evaluación asignada", mensajeAsignado, "EVALUACION_REASIGNADA", dto.getAdminId());
        }

        return savedNueva;
    }
}
