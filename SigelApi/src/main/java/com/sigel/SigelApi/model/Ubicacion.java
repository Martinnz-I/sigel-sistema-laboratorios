package com.sigel.SigelApi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ubicaciones", schema = "sigel")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ubicacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "laboratorio_id", nullable = false)
    private Laboratorio laboratorio;

    @Column(length = 100, nullable = false)
    private String nombre; // Ej: "Gabinete A-2, Estante 3"

    @Column(length = 50)
    private String tipo; // Ej: "gabinete", "mesa", "anaquel"

    @Column(name = "coordenada_x", nullable = false, precision = 5, scale = 2)
    private BigDecimal coordenadaX;

    @Column(name = "coordenada_y", nullable = false, precision = 5, scale = 2)
    private BigDecimal coordenadaY;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}