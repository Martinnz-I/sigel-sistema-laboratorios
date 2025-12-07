package com.sigel.SigelApi.controller;

import com.sigel.SigelApi.dto.ApiResponse;
import com.sigel.SigelApi.dto.PrestamoInternoActivoDTO;
import com.sigel.SigelApi.dto.PrestamoInternoDevolucionRequest;
import com.sigel.SigelApi.dto.PrestamoInternoRequest;
import com.sigel.SigelApi.service.PrestamoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/prestamos")
@RequiredArgsConstructor
public class PrestamoController {
    private final PrestamoService prestamoService;

    @GetMapping("/interno/usuario")
    public ResponseEntity<ApiResponse<PrestamoInternoActivoDTO>> obtenerPrestamosInternosPorUsuario() {
        System.out.println("Obteniendo prestamos activos");
        PrestamoInternoActivoDTO response = prestamoService.obtenerPrestamosInternosActivosPorUsuario();
        System.out.println("Response. " + response);

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(
                        response,
                        "Prestamo interno realizado correctamente"
                ));
    }

    @PostMapping(value = "/solicitar/interno", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> SolicitarPrestamoInterno(
            @Valid @RequestPart("datos") PrestamoInternoRequest request,
            @RequestPart(value = "imagen") MultipartFile fotoPrevia
    ) {
        System.out.println("Procesando equipo para solicitar prestamo: " + request);
        prestamoService.crearPrestamoInterno(request, fotoPrevia);

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(
                        "Prestamo interno realizado correctamente"
                ));
    }

    @PostMapping(value = "/devolver/interno", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> DevolverPrestamoInterno(
            @Valid @RequestPart("datos") PrestamoInternoDevolucionRequest request,
            @RequestPart(value = "imagenes") List<MultipartFile> fotosPosteriores
    ) {
        System.out.println("Devoluciones: " + request);
        prestamoService.devolverPrestamoInterno(request, fotosPosteriores);

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(
                        "Prestamo interno completado correctamente"
                ));
    }
}