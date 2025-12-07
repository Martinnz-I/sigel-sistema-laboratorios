package com.sigel.SigelApi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstudianteDTO {
    private Long id;
    private String matricula;
    private String email;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String fotoPerfilUrl;
    private String grupo;
    private Integer semestre;
    private String especialidad;
    private List<Long> equipoId;
    private boolean conectado;
    private LocalDateTime conectadoEl;
}