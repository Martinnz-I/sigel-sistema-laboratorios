package com.sigel.SigelApi.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChecklistInspeccionDTO {
    @NotNull
    @Pattern(regexp = "excelente|bueno|sucio|muy_sucio")
    private String limpieza;

    @NotNull
    @Pattern(regexp = "sin_danos|rayones_leves|golpes_menores|dano_severo")
    private String integridadFisica;

    @NotNull
    private Boolean accesoriosCompletos;

    @NotNull
    @Pattern(regexp = "funciona_perfectamente|funciona_con_fallas|no_funciona|no_aplica")
    private String funcionalidad;

    @NotNull
    @Pattern(regexp = "intactos|desgastados|rotos|no_aplica")
    private String cablesConectores;

    @NotNull
    private Boolean etiquetasLegibles;
}