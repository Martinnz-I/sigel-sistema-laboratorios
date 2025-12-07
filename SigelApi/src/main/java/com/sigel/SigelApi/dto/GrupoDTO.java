package com.sigel.SigelApi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GrupoDTO {
    private Long id;
    private Integer semestre;
    private String grupo;
    private Long especialidadId;
}