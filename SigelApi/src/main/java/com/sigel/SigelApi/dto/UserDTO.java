package com.sigel.SigelApi.dto;

import com.sigel.SigelApi.enums.UserRole;
import com.sigel.SigelApi.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    private String matricula;
    private String claveDocente;
    private String email;
    private String nombre;
    private String apellidos;
    private UserRole rol;
    private Integer semestre;
    private String grupo;
    private String especialidad;
    private String fotoPerfilUrl;

    public static UserDTO fromEntity(Usuario usuario) {
        return UserDTO.builder()
                .matricula(usuario.getMatricula())
                .claveDocente(usuario.getClaveDocente())
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .apellidos(usuario.getApellidoPaterno() + " " + usuario.getApellidoMaterno())
                .rol(usuario.getRol())
                .semestre(usuario.getGrupo().getSemestre())
                .grupo(usuario.getGrupo().getGrupo())
                .especialidad(usuario.getGrupo().getEspecialidad().getNombre())
                .fotoPerfilUrl(usuario.getFotoPerfilUrl())
                .build();
    }
}