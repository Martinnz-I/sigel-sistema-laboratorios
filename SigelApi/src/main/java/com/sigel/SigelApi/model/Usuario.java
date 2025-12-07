package com.sigel.SigelApi.model;

import com.sigel.SigelApi.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Identificación
    @Column(length = 20, unique = true)
    private String matricula;

    @Column(length = 100, nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    // Información personal
    @Column(length = 100, nullable = false)
    private String nombre;

    @Column(length = 100, nullable = false)
    private String apellidoPaterno;

    @Column(length = 100, nullable = false)
    private String apellidoMaterno;

    private String fotoPerfilUrl;

    // Rol y permisos
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "user_role", nullable = false)
    @Builder.Default
    private UserRole rol = UserRole.ALUMNO;

    // Para alumnos
    @ManyToOne
    @JoinColumn(name = "grupo_id")
    private Grupo grupo;

    // Para maestros
    @Column(length = 20, unique = true)
    private String claveDocente;

    @Builder.Default
    private Boolean activo = true;

    private boolean emailVerificado;

    private LocalDateTime ultimoAcceso;

    // Timestamps
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}