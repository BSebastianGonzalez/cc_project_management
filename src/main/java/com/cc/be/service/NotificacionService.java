package com.cc.be.service;

import com.cc.be.model.Notificacion;
import com.cc.be.repository.NotificacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
}