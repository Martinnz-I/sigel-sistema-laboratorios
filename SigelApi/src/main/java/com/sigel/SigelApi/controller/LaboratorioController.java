package com.sigel.SigelApi.controller;

import com.sigel.SigelApi.dto.ApiResponse;
import com.sigel.SigelApi.dto.LabPickerDTO;
import com.sigel.SigelApi.dto.LaboratorioRequest;
import com.sigel.SigelApi.model.Laboratorio;
import com.sigel.SigelApi.service.LaboratorioService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/laboratorios")
@RequiredArgsConstructor
public class LaboratorioController {

    private final LaboratorioService laboratorioService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Laboratorio>>> obtenerTodos() {
        List<Laboratorio> laboratorios = laboratorioService.obtenerLaboratorios();

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(
                        laboratorios,
                        String.format("Se encontraron %d laboratorios", laboratorios.size())
                ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Laboratorio>> buscarPorId(
            @Parameter(description = "ID del laboratorio a buscar", required = true, example = "1")
            @PathVariable Long id
    ) {
        Laboratorio laboratorio = laboratorioService.buscarPorId(id);

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(laboratorio, "Laboratorio encontrado"));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Laboratorio>> registrar(
            @RequestPart("datos") @Valid LaboratorioRequest request,
            @RequestPart(value = "imagen", required = false) MultipartFile imagen
    ) {
        Laboratorio laboratorio = laboratorioService.construirLaboratorio(request, imagen);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(laboratorio, "Laboratorio registrado exitosamente"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Laboratorio>> actualizar(
            @Parameter(description = "ID del laboratorio a actualizar", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody LaboratorioRequest request
    ) {
        Laboratorio laboratorioActualizado = laboratorioService.actualizar(id, request);

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(laboratorioActualizado, "Laboratorio actualizado exitosamente"));
    }

    @GetMapping("/catalogo")
    public ResponseEntity<ApiResponse<List<LabPickerDTO>>> obtenerCatalogoLaboratorios() {
        List<LabPickerDTO> laboratorios = laboratorioService.obtenerCatalogoLaboratorios();

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(
                        laboratorios,
                        String.format("Se encontraron %d laboratorios", laboratorios.size())
                ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(
            @PathVariable Long id
    ) {
        laboratorioService.eliminar(id);

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(null, "Laboratorio eliminado exitosamente"));
    }
}