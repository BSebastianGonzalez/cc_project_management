package com.cc.be.service;

import com.cc.be.dto.FormatoDTO;
import com.cc.be.dto.ItemFormatoDTO;
import com.cc.be.model.Criterio;
import com.cc.be.model.Formato;
import com.cc.be.model.ItemFormato;
import com.cc.be.repository.CriterioRepository;
import com.cc.be.repository.FormatoRepository;
import com.cc.be.repository.ItemFormatoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FormatoService {
    private final FormatoRepository formatoRepository;
    private final ItemFormatoRepository itemRepository;
    private final CriterioRepository criterioRepository;

    public Formato createFormato(FormatoDTO dto) {

        Formato formato = new Formato();
        formato.setNombre(dto.getNombre());
        formato.setDescripcion(dto.getDescripcion());
        formato.setInstitucion(dto.getInstitucion());
        formato.setActivo(dto.isActivo());

        List<ItemFormato> items = dto.getItems().stream().map(i -> {

            Criterio criterio = criterioRepository.findById(i.getCriterioId())
                    .orElseThrow(() -> new RuntimeException("Criterio no encontrado"));

            ItemFormato item = new ItemFormato();
            item.setNombre(i.getNombre());
            item.setDescripcion(i.getDescripcion());
            item.setPeso(i.getPeso());
            item.setCriterio(criterio);
            item.setFormato(formato);

            return item;
        }).collect(Collectors.toList());

        formato.setItems(items);

        if (formato.getPesoTotal() != 100) {
            throw new RuntimeException("El peso total debe ser exactamente 100.");
        }

        return formatoRepository.save(formato);
    }


    public Formato updateFormato(Long id, FormatoDTO dto) {

        Formato formato = formatoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Formato no encontrado"));

        formato.setNombre(dto.getNombre());
        formato.setDescripcion(dto.getDescripcion());
        formato.setInstitucion(dto.getInstitucion());
        formato.setActivo(dto.isActivo());

        // Primero limpiar items viejos
        formato.getItems().clear();
        formatoRepository.flush();

        // Crear nuevos items
        List<ItemFormato> nuevosItems = dto.getItems().stream().map(i -> {
            ItemFormato item = new ItemFormato();
            item.setNombre(i.getNombre());
            item.setDescripcion(i.getDescripcion());
            item.setPeso(i.getPeso());
            item.setFormato(formato);

            // Vincular criterio si existe
            if (i.getCriterioId() != null) {
                Criterio criterio = criterioRepository.findById(i.getCriterioId())
                        .orElseThrow(() -> new RuntimeException("Criterio no encontrado"));
                item.setCriterio(criterio);
            }

            return item;
        }).collect(Collectors.toList());

        formato.setItems(nuevosItems);

        if (formato.getPesoTotal() != 100) {
            throw new RuntimeException("El peso total debe ser exactamente 100. Actual: " + formato.getPesoTotal());
        }

        return formatoRepository.save(formato);
    }


    public ItemFormato addItem(Long formatoId, ItemFormatoDTO dto) {

        Formato formato = formatoRepository.findById(formatoId)
                .orElseThrow(() -> new RuntimeException("Formato no encontrado"));

        Criterio criterio = criterioRepository.findById(dto.getCriterioId())
                .orElseThrow(() -> new RuntimeException("Criterio no encontrado"));

        ItemFormato item = new ItemFormato();
        item.setNombre(dto.getNombre());
        item.setDescripcion(dto.getDescripcion());
        item.setPeso(dto.getPeso());
        item.setFormato(formato);
        item.setCriterio(criterio);

        int nuevoPeso = formato.getPesoTotal() + dto.getPeso();
        if (nuevoPeso > 100) {
            throw new RuntimeException("Agregar este item supera el peso máximo (100).");
        }

        formato.getItems().add(item);
        formatoRepository.save(formato);

        return item;
    }


    @DeleteMapping("/formatos/{formatoId}/items/{itemId}")
    public ResponseEntity<?> deleteItem(
            @PathVariable Long formatoId,
            @PathVariable Long itemId) {

        Formato formato = formatoRepository.findById(formatoId)
                .orElseThrow(() -> new RuntimeException("Formato no encontrado"));

        ItemFormato item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item no encontrado"));

        if (!item.getFormato().getId().equals(formatoId)) {
            throw new RuntimeException("El item no pertenece a este formato");
        }

        // ESTA ES LA PARTE IMPORTANTE
        formato.getItems().remove(item);
        item.setFormato(null);

        formatoRepository.save(formato);

        return ResponseEntity.ok("Item eliminado correctamente");
    }

    public List<Formato> findAll() {
        return formatoRepository.findAll();
    }

    public Formato findById(Long id) {
        return formatoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Formato no encontrado"));
    }

    public void deleteFormato(Long id) {
        formatoRepository.deleteById(id);
    }

    public ItemFormato cambiarCriterio(Long itemId, Long criterioId) {

        ItemFormato item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item no encontrado"));

        Criterio criterio = criterioRepository.findById(criterioId)
                .orElseThrow(() -> new RuntimeException("Criterio no encontrado"));

        item.setCriterio(criterio);
        return itemRepository.save(item);
    }
}


