package com.sigel.SigelApi.model;

import com.sigel.SigelApi.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios", schema = "sigel")
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

    @Column(length = 255, nullable = false)
    private String passwordHash;

    // Información personal
    @Column(length = 100, nullable = false)
    private String nombre;

    @Column(length = 100, nullable = false)
    private String apellidoPaterno;

    @Column(length = 100, nullable = false)
    private String apellidoMaterno;

    @Column(length = 255)
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

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDate fechaIngreso;

    // Para maestros
    @Column(name = "clave_docente", length = 20, unique = true)
    private String claveDocente;

    // Estado y seguridad
    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @Column(name = "email_verificado", nullable = false)
    @Builder.Default
    private Boolean emailVerificado = false;

    @Column(name = "ultimo_acceso")
    private LocalDateTime ultimoAcceso;

    @Column(name = "reset_token", length = 255)
    private String resetToken;

    @Column(name = "reset_token_expira")
    private LocalDateTime resetTokenExpira;

    // Timestamps
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
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