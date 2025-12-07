package com.sigel.SigelApi.controller;

import com.sigel.SigelApi.dto.ApiResponse;
import com.sigel.SigelApi.dto.GrupoDTO;
import com.sigel.SigelApi.service.GrupoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/grupos")
@RequiredArgsConstructor
public class GrupoController {
    private final GrupoService grupoService;

    @GetMapping("/activos")
    public ResponseEntity<ApiResponse<List<GrupoDTO>>> obtenerListaActivos() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(grupoService.listarGruposActivos(), "Exito"));
    }
}