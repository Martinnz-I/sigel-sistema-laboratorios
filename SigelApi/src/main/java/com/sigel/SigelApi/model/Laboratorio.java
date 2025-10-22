package com.sigel.SigelApi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "laboratorios", schema = "sigel")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Laboratorio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false, unique = true)
    private String codigo; // Ej: "LAB-1"

    @Column(length = 100, nullable = false)
    private String nombre; // Ej: "Laboratorio de Electrónica Básica"

    @Column(name = "coordenada_x", nullable = false, precision = 5, scale = 2)
    private BigDecimal coordenadaX;

    @Column(name = "coordenada_y", nullable = false, precision = 5, scale = 2)
    private BigDecimal coordenadaY;
    @Builder.Default
    private Integer piso = 1;

    @Column(name = "capacidad_alumnos")
    private Integer capacidadAlumnos;

    @ManyToOne(optional = false)
    @JoinColumn(name = "especialidad_id", nullable = false)
    private Especialidad especialidad;

    @ManyToOne
    @JoinColumn(name = "encargado_id")
    private Usuario encargado;


    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "horario_apertura", nullable = false)
    private LocalTime horarioApertura;

    @Column(name = "horario_cierre", nullable = false)
    private LocalTime horarioCierre;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

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