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
@Table(name = "laboratorios")
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

    @Column(nullable = false)
    private String mapaUrl;

    @Column(name = "coordenada_x", nullable = false, precision = 5, scale = 2)
    private BigDecimal coordenadaX;

    @Column(name = "coordenada_y", nullable = false, precision = 5, scale = 2)
    private BigDecimal coordenadaY;

    @Builder.Default
    private int piso = 1;

    private Integer capacidadAlumnos;

    @ManyToOne(optional = false)
    @JoinColumn(name = "especialidad_id", nullable = false)
    private Especialidad especialidad;

    @ManyToOne(optional = false)
    @JoinColumn(name = "encargado_id", nullable = false)
    private Usuario encargado;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false)
    private LocalTime horarioApertura;

    @Column(nullable = false)
    private LocalTime horarioCierre;

    @Builder.Default
    private boolean activo = true;

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