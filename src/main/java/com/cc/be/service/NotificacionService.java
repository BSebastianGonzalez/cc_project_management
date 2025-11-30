package com.cc.be.service;

import com.cc.be.model.Notificacion;
import com.cc.be.repository.NotificacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;

    public Notificacion crearNotificacion(Long usuarioId, String titulo, String mensaje, String tipo, Long remitenteId) {
        Notificacion n = new Notificacion();
        n.setUsuarioId(usuarioId);
        n.setTitulo(titulo);
        n.setMensaje(mensaje);
        n.setTipo(tipo);
        n.setRemitenteId(remitenteId);
        n.setLeida(false);
        n.setFecha(LocalDateTime.now());
        return notificacionRepository.save(n);
    }

    public List<Notificacion> obtenerPorUsuario(Long usuarioId) {
        return notificacionRepository.findByUsuarioIdOrderByFechaDesc(usuarioId);
    }

    public Notificacion marcarComoLeida(Long notificacionId) {
        Notificacion n = notificacionRepository.findById(notificacionId)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));
        if (!n.isLeida()) {
            n.setLeida(true);
            n = notificacionRepository.save(n);
        }
        return n;
    }

    @Transactional
    public int marcarTodasComoLeidas(Long usuarioId) {
        List<Notificacion> pendientes = notificacionRepository.findByUsuarioIdAndLeidaFalse(usuarioId);
        if (pendientes.isEmpty()) return 0;
        pendientes.forEach(n -> n.setLeida(true));
        notificacionRepository.saveAll(pendientes);
        return pendientes.size();
    }
}
