package com.cc.be.controller;

import com.cc.be.dto.FormatoDTO;
import com.cc.be.dto.ItemFormatoDTO;
import com.cc.be.model.Formato;
import com.cc.be.model.ItemFormato;
import com.cc.be.repository.FormatoRepository;
import com.cc.be.repository.ItemFormatoRepository;
import com.cc.be.service.FormatoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/formatos")
@RequiredArgsConstructor
public class FormatoController {

    private final FormatoService formatoService;
    private final FormatoRepository formatoRepository;
    private final ItemFormatoRepository itemFormatoRepository;

    @GetMapping
    public List<Formato> getAll() {
        return formatoService.findAll();
    }

    @GetMapping("/{id}")
    public Formato getById(@PathVariable Long id) {
        return formatoService.findById(id);
    }

    @PostMapping
    public Formato create(@RequestBody FormatoDTO dto) {
        return formatoService.createFormato(dto);
    }

    @PutMapping("/{id}")
    public Formato update(@PathVariable Long id, @RequestBody FormatoDTO dto) {
        return formatoService.updateFormato(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        formatoService.deleteFormato(id);
    }


    @PostMapping("/{id}/items")
    public ItemFormato addItem(@PathVariable Long id, @RequestBody ItemFormatoDTO dto) {
        return formatoService.addItem(id, dto);
    }

    @DeleteMapping("/formatos/{formatoId}/items/{itemId}")
    public ResponseEntity<String> deleteItemFromFormato(
            @PathVariable Long formatoId,
            @PathVariable Long itemId) {

        // 1. Obtener el formato
        Formato formato = formatoRepository.findById(formatoId)
                .orElseThrow(() -> new RuntimeException("Formato no encontrado"));

        // 2. Obtener el item
        ItemFormato item = itemFormatoRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item no encontrado"));

        // 3. Validar que realmente pertenece al formato
        if (!item.getFormato().getId().equals(formatoId)) {
            throw new RuntimeException("El item no pertenece a este formato");
        }

        // 4. Remover correctamente el hijo de la lista del padre
        formato.getItems().remove(item);
        item.setFormato(null); // <- IMPORTANTE para orphanRemoval

        // 5. Guardar el formato
        formatoRepository.save(formato);

        return ResponseEntity.ok("Item eliminado correctamente");
    }

    @PutMapping("/formatos/{id}/estado")
    public ResponseEntity<String> cambiarEstadoFormato(
            @PathVariable Long id,
            @RequestParam boolean activo) {

        Formato formato = formatoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Formato no encontrado"));

        formato.setActivo(activo);

        formatoRepository.save(formato);

        String estado = activo ? "activado" : "desactivado";

        return ResponseEntity.ok("Formato " + estado + " correctamente");
    }

}
