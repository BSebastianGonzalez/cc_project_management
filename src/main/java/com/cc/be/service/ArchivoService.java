package com.cc.be.service;

import com.cc.be.model.Archivo;
import com.cc.be.model.Proyecto;
import com.cc.be.repository.ArchivoRepository;
import com.cc.be.repository.ProyectoRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ArchivoService {

    private final ProyectoRepository proyectoRepository;
    private final ArchivoRepository archivoRepository;
    private final Cloudinary cloudinary;

    public Archivo uploadFile(MultipartFile file, Long proyectoId) throws IOException {

        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado con ID: " + proyectoId));

        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();

        if (contentType == null) {
            throw new IllegalArgumentException("No se pudo determinar el tipo MIME del archivo.");
        }

        // Detectar automáticamente el tipo de recurso
        String resourceType = detectResourceType(contentType);

        // Generar public_id limpio (sin extensión)
        String publicId = cleanPublicId(originalFilename);

        // Subida a Cloudinary
        Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "resource_type", resourceType,
                        "public_id", publicId
                )
        );

        // Crear entidad Archivo
        Archivo archivo = new Archivo();
        archivo.setProyecto(proyecto);
        archivo.setUrlArchivo((String) uploadResult.get("secure_url"));
        archivo.setNombreArchivo(originalFilename);
        archivo.setTipoMime(contentType);

        // Tipo personalizado para diferenciar archivos
        archivo.setTipo(obtenerTipoSegunMime(contentType));

        return archivoRepository.save(archivo);
    }

    // -----------------------------------------------------
    // Detecta tipo de recurso (Cloudinary)
    // -----------------------------------------------------
    private String detectResourceType(String mime) {

        // Imágenes
        Set<String> imageTypes = Set.of(
                "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
        );

        // Videos / audio
        Set<String> videoTypes = Set.of(
                "video/mp4", "video/webm", "video/ogg", "audio/mpeg", "audio/mp3"
        );

        // Documentos (excepto PDF, que queremos visualizar)
        Set<String> rawTypes = Set.of(
                "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "application/vnd.ms-excel",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "text/plain"
        );

        // PDF DEBE ABRIRSE EN EL NAVEGADOR
        if (mime.equals("application/pdf")) {
            return "auto";  // Cloudinary sirve PDF de forma visualizable
        }

        if (imageTypes.contains(mime)) return "image";
        if (videoTypes.contains(mime)) return "video";
        if (rawTypes.contains(mime)) return "raw";

        return "auto"; // fallback
    }


    // -----------------------------------------------------
    // public_id sin extensión
    // -----------------------------------------------------
    private String cleanPublicId(String filename) {

        if (filename == null) return "archivo";

        String baseName = filename.contains(".")
                ? filename.substring(0, filename.lastIndexOf('.'))
                : filename;

        return baseName.replaceAll("[^a-zA-Z0-9_\\-]", "_");
    }

    // -----------------------------------------------------
    // Tipo lógico para tu base de datos
    // -----------------------------------------------------
    private String obtenerTipoSegunMime(String mime) {

        if (mime.equals("application/pdf")) {
            return "PDF_PROYECTO";
        }

        if (mime.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                || mime.equals("application/vnd.ms-excel")) {
            return "EXCEL_AUTOR";
        }

        return "OTRO";
    }

    public String uploadTest(MultipartFile file) throws IOException {

        Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap("resource_type", "auto")
        );

        return (String) uploadResult.get("secure_url");
    }
}
