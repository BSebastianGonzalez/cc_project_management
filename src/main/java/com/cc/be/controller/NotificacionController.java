package com.cc.be.controller;

import com.cc.be.model.Notificacion;
import com.cc.be.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionService notificacionService;

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Notificacion>> obtenerPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(notificacionService.obtenerPorUsuario(usuarioId));
    }

    @PutMapping("/{id}/leer")
    public ResponseEntity<Notificacion> marcarComoLeida(@PathVariable Long id) {
        return ResponseEntity.ok(notificacionService.marcarComoLeida(id));
    }

    @PutMapping("/usuario/{usuarioId}/leer-todas")
    public ResponseEntity<String> marcarTodasComoLeidas(@PathVariable Long usuarioId) {
        int actualizadas = notificacionService.marcarTodasComoLeidas(usuarioId);
        return ResponseEntity.ok("Notificaciones marcadas como leídas: " + actualizadas);
    }
}
