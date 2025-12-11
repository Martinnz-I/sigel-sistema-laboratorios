package com.sigel.SigelApi.service;

import com.google.cloud.storage.*;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.sigel.SigelApi.exceptions.ImageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class StorageService {
    @Autowired
    private Storage storage;

    @Value("${gcp.bucket.name}")
    private String bucketName;

    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/gif",
            "image/webp"
    );

    private static final long MAX_FILE_SIZE = 15 * 1024 * 1024;

    public Map<String, String> subirImagenes(List<MultipartFile> imagenes) {
        Map<String, String> resultados = new LinkedHashMap<>();

        for (int i = 0; i < imagenes.size(); i++) {
            try {
                String url = subirImagen(imagenes.get(i));
                resultados.put("imagen_" + (i + 1), url);
            } catch (Exception e) {
                throw new ImageException("Error al subir imagen " + (i + 1) + ": " + e.getMessage());
            }
        }

        return resultados;
    }

    public String subirImagen(MultipartFile file) {
        try {
            String fileName = generarNombreImagen(file.getOriginalFilename());

            BlobId blobId = BlobId.of(bucketName, fileName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(file.getContentType())
                    .build();

            Blob blob = storage.create(blobInfo, file.getBytes());

            if (blob == null) {
                throw new ImageException("No se pudo subir la imagen correctamente");
            }

            return obtenerUrlPublica(fileName);

        } catch (ImageException e) {
            throw e;
        } catch (IOException e) {
            throw new ImageException("Error al procesar la imagen: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ImageException("Error al subir la imagen al servidor", e);
        }
    }

    public String subirImagenDesdeBytes(byte[] bytes, String nombreArchivo, String contentType) {
        try {
            // Validar tamaño
            if (bytes.length > 5 * 1024 * 1024) { // 5MB
                throw new ImageException("La imagen excede el tamaño máximo permitido");
            }

            String fileName = generarNombreImagen(nombreArchivo);

            BlobId blobId = BlobId.of(bucketName, fileName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(contentType)
                    .build();

            storage.create(blobInfo, bytes);

            return obtenerUrlPublica(fileName);

        } catch (ImageException e) {
            throw e;
        } catch (Exception e) {
            throw new ImageException("Error al subir la imagen al servidor", e);
        }
    }

    public boolean eliminarImagen(String fileName) {
        try {
            BlobId blobId = BlobId.of(bucketName, fileName);

            return storage.delete(blobId);
        } catch (Exception e) {
            throw new ImageException("Error al eliminar la imagen", e);
        }
    }

    public byte[] descargarImagen(String fileName) {
        try {
            Blob blob = storage.get(BlobId.of(bucketName, fileName));

            if (blob == null) {
                throw new ImageException("Imagen no encontrada: " + fileName);
            }

            return blob.getContent();
        } catch (ImageException e) {
            throw e;
        } catch (Exception e) {
            throw new ImageException("Error al descargar la imagen", e);
        }
    }

    public void validarImagen(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ImageException("No se ha proporcionado ningún archivo");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new ImageException(
                    "Formato de imagen no permitido. Formatos aceptados: JPEG, PNG, GIF, WEBP"
            );
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ImageException(
                    "La imagen excede el tamaño máximo permitido de 5MB"
            );
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new ImageException("El archivo no tiene un nombre válido");
        }
    }

    public void validarImagenes(List<MultipartFile> imagenes, int maxImages) {
        if (imagenes == null || imagenes.isEmpty()) {
            throw new ImageException("La lista de imágenes no puede estar vacía");
        }

        if (imagenes.size() > maxImages) {
            throw new ImageException("No puedes subir más de " + maxImages + " imágenes");
        }

        for(MultipartFile imagen : imagenes) {
            validarImagen(imagen);
        }
    }

    private String generarNombreImagen(String originalFileName) {
        String extension = obtenerExtensionArchivo(originalFileName);
        return UUID.randomUUID().toString() + extension;
    }

    private String obtenerExtensionArchivo(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return ".jpg";
        }
        return fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
    }

    private String obtenerUrlPublica(String fileName) {
        return String.format("https://storage.googleapis.com/%s/%s", bucketName, fileName);
    }
}