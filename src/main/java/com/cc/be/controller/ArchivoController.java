package com.cc.be.controller;

import com.cc.be.model.Archivo;
import com.cc.be.service.ArchivoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/archivos")
@RequiredArgsConstructor
public class ArchivoController {

    private final ArchivoService archivoService;

    @PostMapping("/{proyectoId}/upload")
    public ResponseEntity<Archivo> upload(
            @PathVariable Long proyectoId,
            @RequestParam("file") MultipartFile file
    ) throws IOException {

        return ResponseEntity.ok(archivoService.uploadFile(file, proyectoId));
    }

    @PostMapping("/test/upload")
    public ResponseEntity<String> uploadTest(@RequestParam("file") MultipartFile file) {
        try {
            String url = archivoService.uploadTest(file);
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
