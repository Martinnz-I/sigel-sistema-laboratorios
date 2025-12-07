package com.sigel.SigelApi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabPickerDTO {
    private Long id;
    private String nombreLaboratorio;
    private Long especialidadId;
    private String nombreEspecialidad;
    private int capacidadAlumnos;
}