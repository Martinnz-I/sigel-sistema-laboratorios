package com.sigel.SigelApi.controller;

import com.sigel.SigelApi.dto.ApiResponse;
import com.sigel.SigelApi.dto.EspecialidadDTO;
import com.sigel.SigelApi.service.EspecialidadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/especialidades")
@RequiredArgsConstructor
public class EspecialidadController {
    private final EspecialidadService especialidadService;

    @GetMapping("/activos")
    public ResponseEntity<ApiResponse<List<EspecialidadDTO>>> obtenerListaActivos() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(especialidadService.listarEspecialidadesActivas(), "Exito"));
    }
}