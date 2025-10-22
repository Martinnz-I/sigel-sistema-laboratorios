package com.sigel.SigelApi.controller;

import com.sigel.SigelApi.dto.ApiResponse;
import com.sigel.SigelApi.dto.LaboratorioRequest;
import com.sigel.SigelApi.model.Laboratorio;
import com.sigel.SigelApi.service.LaboratorioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/laboratorios")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Laboratorios", description = "API para la gestión de laboratorios académicos")
public class LaboratorioController {

    private final LaboratorioService laboratorioService;

    @GetMapping
    @Operation(
            summary = "Obtener todos los laboratorios",
            description = "Retorna una lista completa de todos los laboratorios registrados en el sistema"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Lista de laboratorios obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    public ResponseEntity<ApiResponse<List<Laboratorio>>> obtenerTodos() {
        List<Laboratorio> laboratorios = laboratorioService.buscarTodos();

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(
                        laboratorios,
                        String.format("Se encontraron %d laboratorios", laboratorios.size())
                ));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar laboratorio por ID",
            description = "Retorna la información detallada de un laboratorio específico"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Laboratorio encontrado exitosamente"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Laboratorio no encontrado"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    public ResponseEntity<ApiResponse<Laboratorio>> buscarPorId(
            @Parameter(description = "ID del laboratorio a buscar", required = true, example = "1")
            @PathVariable Long id
    ) {
        Laboratorio laboratorio = laboratorioService.buscarPorId(id);

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(laboratorio, "Laboratorio encontrado"));
    }

    @PostMapping
    @Operation(
            summary = "Registrar nuevo laboratorio",
            description = "Crea un nuevo laboratorio en el sistema con la información proporcionada"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Laboratorio registrado exitosamente"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Error en validación o datos duplicados"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    public ResponseEntity<ApiResponse<Laboratorio>> registrar(
            @Valid @RequestBody LaboratorioRequest request
    ) {
        Laboratorio laboratorio = laboratorioService.construirLaboratorio(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(laboratorio, "Laboratorio registrado exitosamente"));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar laboratorio",
            description = "Actualiza la información de un laboratorio existente. Solo actualiza los campos que han cambiado."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Laboratorio actualizado exitosamente"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Error en validación o no hay cambios detectados"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Laboratorio no encontrado"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
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

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar laboratorio",
            description = "Elimina un laboratorio del sistema (eliminación lógica o física según configuración)"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Laboratorio eliminado exitosamente"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Laboratorio no encontrado"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    public ResponseEntity<ApiResponse<Void>> eliminar(
            @Parameter(description = "ID del laboratorio a eliminar", required = true, example = "1")
            @PathVariable Long id
    ) {
        laboratorioService.eliminar(id);

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(null, "Laboratorio eliminado exitosamente"));
    }
}