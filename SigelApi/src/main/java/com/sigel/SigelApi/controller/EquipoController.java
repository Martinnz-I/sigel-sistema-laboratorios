package com.sigel.SigelApi.controller;

import com.sigel.SigelApi.dto.*;
import com.sigel.SigelApi.model.Equipo;
import com.sigel.SigelApi.service.EquipoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/equipos")
@RequiredArgsConstructor
public class EquipoController {
    private final EquipoService equipoService;

    @GetMapping("/validacion/{codigo}")
    public ResponseEntity<ApiResponse<Long>> buscarEquipoPorCodigo(
            @PathVariable String codigo
    ) {
        Long response = equipoService.validarEquipoASolicitar(codigo);

        System.out.println("Equipo buscado por codigo: " + response);

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(
                        response,
                        "Exito"
                ));
    }

    @GetMapping("/catalogo")
    public ResponseEntity<ApiResponse<List<EquipoAgrupadoDTO>>> getCatalogoEquipos() {
        List<EquipoAgrupadoDTO> equipos = equipoService.obtenerCatalogoEquipos();

        if (equipos.isEmpty()) {
            return ResponseEntity
                    .ok(ApiResponse.success(equipos, "No hay equipos disponibles en este momento"));
        }

        return ResponseEntity
                .ok(ApiResponse.success(equipos, "Catálogo de equipos obtenido correctamente"));
    }

    @GetMapping("/catalogo/{id}")
    public ResponseEntity<ApiResponse<EquipoDTO>> buscarEquipoPorIdDTO(@PathVariable Long id) {
        EquipoDTO response = equipoService.buscarEquipoPorIdDTO(id);

        return ResponseEntity
                .ok(ApiResponse.success(response, "Equipo encontrado correctamente"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Equipo>> buscarEquipoPorId(@PathVariable Long id) {
        Equipo equipo = equipoService.buscarEquipoPorId(id);

        return ResponseEntity
                .ok(ApiResponse.success(equipo, "Equipo encontrado correctamente"));
    }

    @PostMapping(value = "/registro", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<NewEquipoResponse>> registro(
            @RequestPart("datos") @Valid NewEquipoRequest request,
            @RequestPart(value = "imagenes") @Size(min = 1, max = 5,
                    message = "Debe proporcionar entre 1 y 5 imágenes") List<MultipartFile> imagenes
    ) {
        Equipo equipo = equipoService.registrarEquipo(request, imagenes);

        NewEquipoResponse response = NewEquipoResponse.fromEntity(equipo);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Equipo creado correctamente"));
    }
}