package com.sigel.SigelApi.controller;

import com.sigel.SigelApi.dto.*;
import com.sigel.SigelApi.model.SesionLaboratorio;
import com.sigel.SigelApi.service.SesionLaboratorioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sesiones-laboratorio")
@RequiredArgsConstructor
public class SesionLaboratorioController {
    private final SesionLaboratorioService sesionLaboratorioService;

    @PostMapping("/crear")
    public ResponseEntity<ApiResponse<SesionLabResponse>> nuevaSesionLaboratorio(
            @RequestBody @Valid SesionLabRequest request
    ) {
        SesionLaboratorio sesion = sesionLaboratorioService.nuevaSesionLaboratorio(request);

        SesionLabResponse response = SesionLabResponse.fromEntity(sesion);


        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Sesión de laboratorio creada correctamente"));
    }

    @GetMapping("/{sesionId}/estudiantes")
    public ResponseEntity<ApiResponse<List<EstudianteDTO>>> obtenerEstudiantes(@PathVariable Long sesionId) {
        return ResponseEntity.ok(ApiResponse.success(sesionLaboratorioService.obtenerEstudiantesPorSesion(sesionId), null));
    }

    @GetMapping("/{sesionId}/equipos")
    public ResponseEntity<ApiResponse<List<EquipoLabMostrarDTO>>> obtenerEquipos(@PathVariable Long sesionId) {
        return ResponseEntity.ok(ApiResponse.success(sesionLaboratorioService.obtenerEquiposPorSesion(sesionId),null));
    }
}